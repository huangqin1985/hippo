-- report definition


CREATE TABLE account (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	name VARCHAR(100) NOT NULL,  --账号名称
	number INTEGER NOT NULL,	--号码
	company VARCHAR(100) NOT NULL,	--经纪商
	server VARCHAR(100) NOT NULL,	--服务器
	currency VARCHAR(10) NOT NULL,	--货币
	leverage INTEGER NOT NULL,	--杠杆
	balance DECIMAL(10,2) NOT NULL,	--余额
	time_zone INTEGER ,	--时区
	client_name VARCHAR(100) NOT NULL, -- 客户名称
	stop_out_level VARCHAR(50) NOT NULL, -- 爆仓条件
	symbol_margin TEXT NOT NULL, -- 产品预付款
	create_time VARCHAR(50) NOT NULL,	--创建时间
	update_time VARCHAR(50) NOT NULL,	--更新时间
	connect_time VARCHAR(50) NOT NULL,	--连接时间
	CONSTRAINT account_PK UNIQUE (`name`)
);

CREATE TABLE report (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	account_id INTEGER NOT NULL,	--账号id
	"type" VARCHAR(50) NOT NULL,	--类型 week-按周 month-按月 
	start_date VARCHAR(50) NOT NULL,	--开始日期
	end_date VARCHAR(50) NOT NULL,	--结束日期
	pre_balance DECIMAL(10,2) NOT NULL DEFAULT 0,	--上期余额
	pre_equity DECIMAL(10,2) NOT NULL DEFAULT 0,	--上期净值
	balance DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 余额
	equity DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 净值
	real_profit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 真实利润
	commission DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 手续费
	swap DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 过夜费
	profit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 获利
	lots DECIMAL(10,2) NOT NULL DEFAULT 0,		--交易量
	order_num INTEGER NOT NULL DEFAULT 0,	--订单数
	deposit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 入金
	withdraw DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 出金
	other DECIMAL(10,2) NOT NULL DEFAULT 0,	-- 其他
	max_equity DECIMAL(10,2) ,	-- 最大净值
	min_equity DECIMAL(10,2) ,	-- 最小净值
	max_real_profit DECIMAL(10,2) ,	-- 最大利润
	min_real_profit DECIMAL(10,2) ,	-- 最小利润
	max_profit DECIMAL(10,2) ,	-- 最大盈亏
	min_profit DECIMAL(10,2) ,	-- 最小盈亏
	max_margin DECIMAL(10,2) ,	-- 最大预付款
	min_margin_rate DECIMAL(10,2) ,	-- 最小预付款比率
	extra INTEGER text,	--额外
	update_time VARCHAR(50) NOT NULL,	--更新时间
	create_time VARCHAR(50) NOT NULL,	--创建时间
	CONSTRAINT report_PK UNIQUE (`account_id`,`type`,`start_date`)
);

-- trade_fund definition

CREATE TABLE trade_fund (
	account_id INTEGER NOT NULL,	--账号id
	ticket VARCHAR(50) NOT NULL,	--订单号
	open_time VARCHAR(50) NOT NULL,	--开盘时间
	profit DECIMAL(10,2) NOT NULL,	--利润
	comment VARCHAR(50),	--注释
	CONSTRAINT trade_fund_PK UNIQUE (`account_id`, `ticket`)
);
CREATE INDEX trade_fund_IDX ON trade_fund (`account_id`, `open_time`);


-- trade_order definition

CREATE TABLE trade_order (
	account_id INTEGER NOT NULL,	--账号id
	ticket VARCHAR(50) NOT NULL,	--订单号
	open_time VARCHAR(50) NOT NULL,	--开盘时间
	close_time VARCHAR(50) NOT NULL,	--收盘时间
	symbol VARCHAR(50) NOT NULL,	--交易品种
	lots DECIMAL(10,2) NOT NULL,		--交易量
	commission DECIMAL(10,2) NOT NULL,	--手续费
	swap DECIMAL(10,2) NOT NULL,		--过夜费
	profit DECIMAL(10,2) NOT NULL,	--利润
	real_profit DECIMAL(10,2) NOT NULL,	--真实利润
	"type" VARCHAR(50) NOT NULL,	--类型 buy-买入 sell-卖出
	open_price VARCHAR(50) NOT NULL,	--开始价格
	close_price VARCHAR(50) NOT NULL,	--结束价格
	stop_loss VARCHAR(50),	--止损价格
	take_profit VARCHAR(50),	--获利价格
	comment VARCHAR(50),	--注释
	CONSTRAINT trade_order_PK UNIQUE (`account_id`, `ticket`)
);
CREATE INDEX trade_order_IDX ON trade_order (`account_id`, `close_time`);


update trade_fund set open_time = DATETIME(open_time, '-5 hours') where account_id > 0; 
update trade_order  set open_time = DATETIME(open_time, '-5 hours'), close_time  = DATETIME(close_time, '-5 hours') where account_id > 0;

delete from account  where id  > 1;
delete from report  where account_id  > 1;
delete from trade_fund  where account_id  > 1;
delete from trade_order  where account_id  > 1;