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
   //查询时区
    if (!isSetTimeZone) {
      int timeZone = TimeHour(TimeCurrent() - TimeGMT());
      string data = "setTimeZone:";

      data += "{timeZone:" + timeZone + "}";
      data += "\n";
      glbClientSocket.Send(data);
      isSetTimeZone = true;
    }
  }
//+------------------------------------------------------------------+
//| Timer function                                                   |
//+------------------------------------------------------------------+
void OnTimer()
  {
   // 与服务器失去连接
   if (glbClientSocket && !glbClientSocket.IsSocketConnected()) {
      Print("Client disconnected.");
      ExpertRemove();  
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
    
    if (AccountMargin() > 0) {
      glbClientSocket.Send(getPosition());
    }
    
    // 每小时
    datetime curTime = TimeLocal();
    if (TimeMinute(curTime) == 0 && TimeSeconds(curTime) == 0) {
      string data = "margin:{" + getSymbolMarginData() + "}";
      data += "\n";
      
      glbClientSocket.Send(data);
    }
    
  }

/*
   Socket 连接服务器
   
*/
void doConnect() {
   if (!glbClientSocket) {
      glbClientSocket = new ClientSocket(Hostname, ServerPort);
      if (glbClientSocket.IsSocketConnected()) {
         Print("Client connection succeeded");
         
      } else {
         Print("Client connection failed");
         ExpertRemove();  // 退出
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
   data += ",histories:" + getHistoryData(0, OrdersHistoryTotal()) + ",";
   
   data += getSymbolMarginData();
     
   data += "}\n";
   
   return data;
}

string getSymbolMarginData() {
   string data = "symbolMargins:{buy:[";
    int buyNum = SymbolsTotal(true);
   for(int pos=0;pos<buyNum;pos++) 
    { 
       string symbolName = SymbolName(pos, true);
       data += "{";
       data += "symbol:\"" + symbolName + "\",";
       data += "type:\"buy\",lots:1.00,";
       data += "margin:" + NormalizeDouble(AccountEquity() - AccountFreeMarginCheck(symbolName, OP_BUY, 1), 2);
       data += "}";
 
       if (pos < buyNum - 1) {
         data += ",";
       }
       
     }
     data += "],";
     data += "sell:[";
    int sellNum = SymbolsTotal(true);
   for(int pos=0;pos<sellNum;pos++) 
    { 
       string symbolName = SymbolName(pos, true);
       data += "{";
       data += "symbol:\"" + symbolName + "\",";
       data += "type:\"sell\",lots:1.00,";
       data += "margin:" + NormalizeDouble(AccountEquity() - AccountFreeMarginCheck(symbolName, OP_SELL, 1), 2);
       data += "}";
 
       if (pos < sellNum - 1) {
         data += ",";
       }
       
     }
     data += "]}";
     return data;
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
       } else if (type == 6) {
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
      + ",margin:" + NormalizeDouble(AccountMargin(),2) + ",serverTime:\"" + TimeCurrent() + "\"}\n";
   
   /*
   data += "orders:[";
  
   int total = OrdersTotal();
   
   for(int pos=0;pos<total;pos++) 
    { 
       if(OrderSelect(pos,SELECT_BY_POS)==false) {
         break;
       }
      
       int digits = SymbolInfoInteger(OrderSymbol(), SYMBOL_DIGITS);
       double profit = NormalizeDouble(OrderCommission(),2) + NormalizeDouble(OrderSwap(),2) +NormalizeDouble(OrderProfit(),2);
 
       data += "{";
       data += "openTime:\"" + OrderOpenTime() + "\",";
       data += "symbol:\"" + OrderSymbol() + "\",";
       data += "lots:" + OrderLots() + ",";
       data += "profit:" + profit + ",";
       data += "type:" + OrderType() + ",";
       data += "closePrice:\"" + DoubleToString(OrderClosePrice(),digits) + "\"";
       data += "}";
 
       if (pos < total - 1) {
         data += ",";
       }
       
     }
     data += "]}\n";
     */
     return data;
}