//+------------------------------------------------------------------+
//|                                                        hippo.mq4 |
//|                        Copyright 2022, MetaQuotes Software Corp. |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "Copyright 2022, MetaQuotes Software Corp."
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict
//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+

#include <socket-library-mt4-mt5.mqh>


// --------------------------------------------------------------------
// EA user inputs
// --------------------------------------------------------------------

input string   Hostname = "localhost";    // Server hostname or IP address
ushort   ServerPort = 23456;        // Server port
ClientSocket * glbClientSocket = NULL;
bool isSetTimeZone = false;

int historyTotal;
string firstTicket = NULL;

int OnInit()
  {
//--- create timer
   EventSetTimer(1);
   
   // 初始化历史订单状态
   historyTotal = OrdersHistoryTotal();
   if(OrderSelect(historyTotal - 1, SELECT_BY_POS, MODE_HISTORY)) 
   {  
    firstTicket = OrderTicket(); 
   } else {
      firstTicket = NULL;
   }
   
   doConnect();
   // 发送连接数据
   glbClientSocket.Send(getConnectData());

   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
  void OnDeinit(const int reason)
{
   if (glbClientSocket) {
      delete glbClientSocket;
      glbClientSocket = NULL;
   }
   EventKillTimer();
}
//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
  {

  }
//+------------------------------------------------------------------+
//| Timer function                                                   |
//+------------------------------------------------------------------+
void OnTimer()
  {
   // 与服务器失去连接
   if (glbClientSocket && !glbClientSocket.IsSocketConnected()) {
      Print("Client disconnected.");
      doConnect();
      return;
   }
   
   int total = OrdersHistoryTotal();
   if(OrderSelect(total - 1, SELECT_BY_POS, MODE_HISTORY)) 
   {  
      string ticket = OrderTicket();
      // 历史订单总数变化，第一个订单号变化
      if (historyTotal != total) {
      
         if (firstTicket != ticket) {
            
            glbClientSocket.Send(getNewHistory(historyTotal, total));
         
            historyTotal = total;
            firstTicket = ticket;
         } else {
            historyTotal = total;
         }
      }
   } else {
      historyTotal = total;
   }

    // 
    datetime curTime = TimeLocal();
    if (TimeSeconds(curTime) % 5 == 0) {
      string data = "setMarket:" + getMarketData();
      data += "\n";
      
      glbClientSocket.Send(data);
      
      glbClientSocket.Send(getPosition());
    }
    
  }

/*
   Socket 连接服务器
   
*/
void doConnect() {
   if (!glbClientSocket ||  !glbClientSocket.IsSocketConnected()) {
      glbClientSocket = new ClientSocket(Hostname, ServerPort);
      if (glbClientSocket.IsSocketConnected()) {
         Print("Client connection succeeded");
         glbClientSocket.Send(getConnectData());
      } else {
         Print("Client connection failed");
      }
  }
}

string getConnectData() {
   string data = "connect:";
   
   string stopOutLevel;
    int level=AccountStopoutLevel(); 
      if(AccountStopoutMode()==0) 
        stopOutLevel = level + "%"; 
      else 
        stopOutLevel = level +  AccountCurrency();
  
   data += "{number:" + AccountNumber() + ",company:\"" + AccountCompany() +  "\",currency:\"" + AccountCurrency() +
         "\",leverage:\"" + AccountLeverage() + "\",server:\"" + AccountServer() +  "\",balance:\"" + 
         NormalizeDouble(AccountBalance(),2) +  "\",stopOutLevel:   \"" + stopOutLevel +
          "\",clientName:\"" + AccountName() + "\"";
   data += ",histories:" + getHistoryData(0, OrdersHistoryTotal());

   data += "}\n";
   
   return data;
}
double getMargin(string symbolName) {
   double requiredMargin = MarketInfo(symbolName, MODE_MARGINREQUIRED);
   
   return requiredMargin;
}
double getPointProfit(string symbolName) {
   double pointProfit = 0;
   
   int profitMode = MarketInfo(symbolName, MODE_PROFITCALCMODE);
   if (profitMode == 0) {
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_VALUE);

       } 
       //CFD
       else if (profitMode == 1) {
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE);
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          pointProfit = pointProfit * price;
       } 
       //Futures
       else if (profitMode == 2){
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE);
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          pointProfit = pointProfit * price;
       }
   
   return pointProfit;
}
double getSwap(string symbolName, int type) {
      double buySwapProfit = SymbolInfoDouble(symbolName, SYMBOL_SWAP_LONG);
       double sellSwapProfit = SymbolInfoDouble(symbolName, SYMBOL_SWAP_SHORT);
       
       int profitMode = MarketInfo(symbolName, MODE_PROFITCALCMODE);
      //Forex 
       if (profitMode == 0) {
          double pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_VALUE);
          
          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * pointProfit;
             sellSwapProfit = sellSwapProfit * 3 * pointProfit;
          } else {
            buySwapProfit = buySwapProfit * pointProfit;
            sellSwapProfit = sellSwapProfit * pointProfit;
          }
       } 
       //CFD
       else if (profitMode == 1) {
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          
          double size = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE);

          buySwapProfit = buySwapProfit * size;
          sellSwapProfit = sellSwapProfit * size;

          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * price;
             sellSwapProfit = sellSwapProfit * 3 * price;
          } else {
            buySwapProfit = buySwapProfit * price;
            sellSwapProfit = sellSwapProfit * price;
          }
       } 
       //Futures
       else if (profitMode == 2){
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          
          double size = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE);

          buySwapProfit = buySwapProfit * size;
          sellSwapProfit = sellSwapProfit * size;

          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * price;
             sellSwapProfit = sellSwapProfit * 3 * price;
          } else {
            buySwapProfit = buySwapProfit * price;
            sellSwapProfit = sellSwapProfit * price;
          }
       }
       
       if (type == ORDER_TYPE_BUY) {
         return buySwapProfit;
          
       } else if (type == ORDER_TYPE_SELL) {
         return sellSwapProfit; 
       } else {
         return 0;
       }
}
string getMarketData() {
   string data = "[";
    int buyNum = SymbolsTotal(true);
   for(int pos=0;pos<buyNum;pos++) 
    { 
       string symbolName = SymbolName(pos, true);
       if (SymbolInfoInteger(symbolName, SYMBOL_TRADE_MODE) == 0) {
         continue;
       }
       double pointProfit = 0;
       
       double buySwapProfit = SymbolInfoDouble(symbolName, SYMBOL_SWAP_LONG);
       double sellSwapProfit = SymbolInfoDouble(symbolName, SYMBOL_SWAP_SHORT);
       
       int profitMode = MarketInfo(symbolName, MODE_PROFITCALCMODE);
       //Forex 
       if (profitMode == 0) {
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_VALUE);
          
          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * pointProfit;
             sellSwapProfit = sellSwapProfit * 3 * pointProfit;
          } else {
            buySwapProfit = buySwapProfit * pointProfit;
            sellSwapProfit = sellSwapProfit * pointProfit;
          }
       } 
       //CFD
       else if (profitMode == 1) {
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE);
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          pointProfit = pointProfit * price;
          
          double size = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE);

          buySwapProfit = buySwapProfit * size;
          sellSwapProfit = sellSwapProfit * size;

          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * price;
             sellSwapProfit = sellSwapProfit * 3 * price;
          } else {
            buySwapProfit = buySwapProfit * price;
            sellSwapProfit = sellSwapProfit * price;
          }
       } 
       //Futures
       else if (profitMode == 2){
          pointProfit = SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE);
          
          double price = getProfitCurrencyBid(SymbolInfoString(symbolName, SYMBOL_CURRENCY_PROFIT));
          pointProfit = pointProfit * price;
          
          double size = SymbolInfoDouble(symbolName, SYMBOL_TRADE_TICK_SIZE) * SymbolInfoDouble(symbolName, SYMBOL_TRADE_CONTRACT_SIZE);

          buySwapProfit = buySwapProfit * size;
          sellSwapProfit = sellSwapProfit * size;

          int roll3Days = SymbolInfoInteger(symbolName, SYMBOL_SWAP_ROLLOVER3DAYS);
          if (roll3Days == DayOfWeek()) {
             buySwapProfit = buySwapProfit * 3 * price;
             sellSwapProfit = sellSwapProfit * 3 * price;
          } else {
            buySwapProfit = buySwapProfit * price;
            sellSwapProfit = sellSwapProfit * price;
          }
       }
       int swapMode = MarketInfo(symbolName, MODE_SWAPTYPE);
       if (swapMode == 0) {
       
       } else if (swapMode == 1) {
       
       } else if (swapMode == 2) {
       
       } else if (swapMode == 3) {
       
       }

       double requiredMargin = MarketInfo(symbolName, MODE_MARGINREQUIRED);
       int digits = SymbolInfoInteger(symbolName, SYMBOL_DIGITS);

       data += "{";
       data += "symbol:\"" + symbolName + "\",";
       data += "pointProfit:" +  NormalizeDouble(pointProfit, 3) + ",";
       data += "buySwapProfit:" +  NormalizeDouble(buySwapProfit, 3) + ",";
       data += "sellSwapProfit:" + NormalizeDouble(sellSwapProfit, 3) + ",";
       data += "requiredMargin:" + requiredMargin + ",";
       data += "digits:" + digits + ",";
       data += "buyPrice:" + NormalizeDouble(SymbolInfoDouble(symbolName, SYMBOL_ASK), digits) + ",";
       data += "sellPrice:" + NormalizeDouble(SymbolInfoDouble(symbolName, SYMBOL_BID), digits) + ",";
       data += "spread:" + SymbolInfoInteger(symbolName, SYMBOL_SPREAD) + ",";
       string path = SymbolInfoString(symbolName, SYMBOL_PATH);
       
       data += "path:\"" + StringSubstr(path, 0, StringFind(path, "\\", 0)) + "\",";
       data += "allowTrade:" + (MarketInfo(symbolName, MODE_TRADEALLOWED) == 1);
       data += "}";
       data += ",";
     }
     data = StringSubstr(data, 0, StringLen(data) - 1);
     data += "]";
     return data;
}

double getProfitCurrencyBid(string profitCurrency)
{
   string accountCurrency = AccountInfoString(ACCOUNT_CURRENCY);
   
   if (profitCurrency == accountCurrency) {
      return 1;
   }
   
   double rvalue = 0;//receiving the value of the requested property.
   if(SymbolInfoDouble(profitCurrency + accountCurrency, SYMBOL_BID, rvalue))//Symbol Exist
   {
      return SymbolInfoDouble(profitCurrency + accountCurrency, SYMBOL_BID);
   } 
   else if(SymbolInfoDouble(accountCurrency + profitCurrency, SYMBOL_BID, rvalue))//Symbol Exist
   {
      return 1 / SymbolInfoDouble(accountCurrency + profitCurrency, SYMBOL_BID);
   }
   else
   {

      return 0;
   }
}

string getOrderStr(string typeStr) {
    
    int digits = SymbolInfoInteger(OrderSymbol(), SYMBOL_DIGITS);
 
    string data = "{";
    data += "ticket:\"" + OrderTicket() + "\",";
    data += "openTime:\"" + OrderOpenTime() + "\",";
    data += "closeTime:\"" + OrderCloseTime() + "\",";
    data += "symbol:\"" + OrderSymbol() + "\",";
    data += "lots:" + OrderLots() + ",";
    data += "commission:" + NormalizeDouble(OrderCommission(),2) + ",";
    data += "swap:" + NormalizeDouble(OrderSwap(),2) + ",";
    data += "profit:" + NormalizeDouble(OrderProfit(),2) + ",";
    data += "type:\"" + typeStr + "\",";
    data += "openPrice:\"" + DoubleToString(OrderOpenPrice(),digits) + "\",";
    data += "closePrice:\"" + DoubleToString(OrderClosePrice(),digits) + "\",";
    data += "stopLoss:\"" + DoubleToString(OrderStopLoss(),digits) + "\",";
    data += "takeProfit:\"" + DoubleToString(OrderTakeProfit(),digits) + "\",";
    string comment = OrderComment();
    StringReplace(comment, "\"", "\\\"");
    data += "comment:\"" + comment + "\"";
    data += "}";
    return data;
}
string getHistory() {
   string data = "history:";
   data += "{balance:" + NormalizeDouble(AccountBalance(),2) + ",tradeOrders:";
   data += getHistoryData(0, OrdersHistoryTotal());
   data += "}\n";
   
   return data;
}
string getNewHistory(int start, int end) {
   string data = "orders:";
   data += "{number:" + AccountNumber() + ",balance:" + NormalizeDouble(AccountBalance(),2) + ",tradeOrders:";
   data += getHistoryData(start, end);
   data += "}\n";
   
   return data;
}
string getHistoryData(int startIdx, int endIdx) {
   string data = ""; // 订单数据
   
   data += "[";
  
   for(int i=startIdx;i<endIdx;i++) 
    { 
     //---- check selection result 
     if(OrderSelect(i,SELECT_BY_POS,MODE_HISTORY)==false) 
       { 
        Print("获取订单发生错误 (",GetLastError(),")"); 
        break; 
       }
       
       int type = OrderType();
       string typeStr;
       if (type == 0) {
         typeStr = "buy";
       } else if (type == 1) {
         typeStr = "sell";
       } else if (type == 2) {
         typeStr = "buy limit";
       } else if (type == 3) {
         typeStr = "sell limit";
       } else if (type == 4) {
         typeStr = "buy stop";
       } else if (type == 5) {
         typeStr = "sell stop";
       }  else if (type == 6) {
         typeStr = "balance";
   	 } else {
         //Print("忽略订单," + OrderTicket() + ",type=" + type); 
         continue;
       }
       data += getOrderStr(typeStr);
 
       if (i < endIdx - 1) {
         data += ",";
       }
       
     }
  
     data += "]";
     return data;
}

string getPosition() {
   string data = "position:{profit:" + NormalizeDouble(AccountProfit(),2) + ",equity:" + NormalizeDouble(AccountEquity(),2)
      + ",margin:" + NormalizeDouble(AccountMargin(),2) + ",serverTime:\"" + TimeCurrent() + "\""
      + ",freeMargin:" + NormalizeDouble(AccountFreeMargin(),2) + ",marginLevel:" + NormalizeDouble(AccountInfoDouble(ACCOUNT_MARGIN_LEVEL),2);
   

   data += ",orders:[";
  
   int total = OrdersTotal();
   
   for(int pos=0;pos<total;pos++) 
    { 
       if(OrderSelect(pos,SELECT_BY_POS)==false) {
         break;
       }
      
       int digits = SymbolInfoInteger(OrderSymbol(), SYMBOL_DIGITS);
       double profit = NormalizeDouble(OrderCommission(),2) + NormalizeDouble(OrderSwap(),2) +NormalizeDouble(OrderProfit(),2);
       
       double slDiffPrice = 0;
       double tpDiffPrice = 0;
       double maxLoss = 0;
       double maxProfit = 0;
       
       if (OrderStopLoss() != 0) {
         if (OrderType() == ORDER_TYPE_BUY) {
            slDiffPrice = NormalizeDouble(OrderOpenPrice() - OrderStopLoss(), digits);
          } else if (OrderType() == ORDER_TYPE_SELL) {
            slDiffPrice = NormalizeDouble(OrderStopLoss() - OrderOpenPrice(), digits);
          }
       }
       if (OrderTakeProfit() != 0) {
         if (OrderType() == ORDER_TYPE_BUY) {
            tpDiffPrice = NormalizeDouble(OrderTakeProfit() - OrderOpenPrice(), digits);
          } else if (OrderType() == ORDER_TYPE_SELL) {
            tpDiffPrice = NormalizeDouble(OrderOpenPrice() - OrderTakeProfit(), digits);
          }
       }
       int slPoints = slDiffPrice / SymbolInfoDouble(OrderSymbol(), SYMBOL_POINT);
       int tpPoints = tpDiffPrice / SymbolInfoDouble(OrderSymbol(), SYMBOL_POINT);
       
       double pointValue = getPointProfit(OrderSymbol());
       
       maxLoss = slPoints * pointValue * OrderLots() * -1;
       maxProfit = tpPoints * pointValue * OrderLots();
       
       data += "{";
       data += "openTime:\"" + OrderOpenTime() + "\",";
       data += "symbol:\"" + OrderSymbol() + "\",";
       data += "lots:" + OrderLots() + ",";
       data += "profit:" + profit + ",";
       data += "type:" + OrderType() + ",";
       data += "openPrice:\"" + DoubleToString(OrderOpenPrice(),digits) + "\",";
       data += "closePrice:\"" + DoubleToString(OrderClosePrice(),digits) + "\",";
       data += "takeProfit:\"" + DoubleToString(OrderTakeProfit(),digits) + "\",";
       data += "stopLoss:\"" + DoubleToString(OrderStopLoss(),digits) + "\",";
       data += "margin:\"" + (getMargin(OrderSymbol())  * OrderLots()) + "\",";
       data += "maxLoss:" + maxLoss + ",";
       data += "maxProfit:" + maxProfit + ",";
       data += "swap:" + (getSwap(OrderSymbol(), OrderType())  * OrderLots());
       data += "}";
       
 
       if (pos < total - 1) {
         data += ",";
       }
       
     }
     data += "]";
     data += "}\n";

     return data;
}