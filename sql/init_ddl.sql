-- report definition


CREATE TABLE account (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	name VARCHAR(100) NOT NULL,  --�˺�����
	number INTEGER NOT NULL,	--����
	company VARCHAR(100) NOT NULL,	--������
	server VARCHAR(100) NOT NULL,	--������
	currency VARCHAR(10) NOT NULL,	--����
	leverage INTEGER NOT NULL,	--�ܸ�
	balance DECIMAL(10,2) NOT NULL,	--���
	time_zone INTEGER ,	--ʱ��
	client_name VARCHAR(100) NOT NULL, -- �ͻ�����
	stop_out_level VARCHAR(50) NOT NULL, -- ��������
	symbol_margin TEXT NOT NULL, -- ��ƷԤ����
	create_time VARCHAR(50) NOT NULL,	--����ʱ��
	update_time VARCHAR(50) NOT NULL,	--����ʱ��
	connect_time VARCHAR(50) NOT NULL,	--����ʱ��
	CONSTRAINT account_PK UNIQUE (`name`)
);

CREATE TABLE report (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	account_id INTEGER NOT NULL,	--�˺�id
	"type" VARCHAR(50) NOT NULL,	--���� week-���� month-���� 
	start_date VARCHAR(50) NOT NULL,	--��ʼ����
	end_date VARCHAR(50) NOT NULL,	--��������
	pre_balance DECIMAL(10,2) NOT NULL DEFAULT 0,	--�������
	pre_equity DECIMAL(10,2) NOT NULL DEFAULT 0,	--���ھ�ֵ
	balance DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ���
	equity DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ��ֵ
	real_profit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ��ʵ����
	commission DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ������
	swap DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ��ҹ��
	profit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ����
	lots DECIMAL(10,2) NOT NULL DEFAULT 0,		--������
	order_num INTEGER NOT NULL DEFAULT 0,	--������
	deposit DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ���
	withdraw DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ����
	other DECIMAL(10,2) NOT NULL DEFAULT 0,	-- ����
	max_equity DECIMAL(10,2) ,	-- ���ֵ
	min_equity DECIMAL(10,2) ,	-- ��С��ֵ
	max_real_profit DECIMAL(10,2) ,	-- �������
	min_real_profit DECIMAL(10,2) ,	-- ��С����
	max_profit DECIMAL(10,2) ,	-- ���ӯ��
	min_profit DECIMAL(10,2) ,	-- ��Сӯ��
	max_margin DECIMAL(10,2) ,	-- ���Ԥ����
	min_margin_rate DECIMAL(10,2) ,	-- ��СԤ�������
	extra INTEGER text,	--����
	update_time VARCHAR(50) NOT NULL,	--����ʱ��
	create_time VARCHAR(50) NOT NULL,	--����ʱ��
	CONSTRAINT report_PK UNIQUE (`account_id`,`type`,`start_date`)
);

-- trade_fund definition

CREATE TABLE trade_fund (
	account_id INTEGER NOT NULL,	--�˺�id
	ticket VARCHAR(50) NOT NULL,	--������
	open_time VARCHAR(50) NOT NULL,	--����ʱ��
	profit DECIMAL(10,2) NOT NULL,	--����
	comment VARCHAR(50),	--ע��
	CONSTRAINT trade_fund_PK UNIQUE (`account_id`, `ticket`)
);
CREATE INDEX trade_fund_IDX ON trade_fund (`account_id`, `open_time`);


-- trade_order definition

CREATE TABLE trade_order (
	account_id INTEGER NOT NULL,	--�˺�id
	ticket VARCHAR(50) NOT NULL,	--������
	open_time VARCHAR(50) NOT NULL,	--����ʱ��
	close_time VARCHAR(50) NOT NULL,	--����ʱ��
	symbol VARCHAR(50) NOT NULL,	--����Ʒ��
	lots DECIMAL(10,2) NOT NULL,		--������
	commission DECIMAL(10,2) NOT NULL,	--������
	swap DECIMAL(10,2) NOT NULL,		--��ҹ��
	profit DECIMAL(10,2) NOT NULL,	--����
	real_profit DECIMAL(10,2) NOT NULL,	--��ʵ����
	"type" VARCHAR(50) NOT NULL,	--���� buy-���� sell-����
	open_price VARCHAR(50) NOT NULL,	--��ʼ�۸�
	close_price VARCHAR(50) NOT NULL,	--�����۸�
	stop_loss VARCHAR(50),	--ֹ��۸�
	take_profit VARCHAR(50),	--�����۸�
	comment VARCHAR(50),	--ע��
	CONSTRAINT trade_order_PK UNIQUE (`account_id`, `ticket`)
);
CREATE INDEX trade_order_IDX ON trade_order (`account_id`, `close_time`);


update trade_fund set open_time = DATETIME(open_time, '-5 hours') where account_id > 0; 
update trade_order  set open_time = DATETIME(open_time, '-5 hours'), close_time  = DATETIME(close_time, '-5 hours') where account_id > 0;

delete from account  where id  > 1;
delete from report  where account_id  > 1;
delete from trade_fund  where account_id  > 1;
delete from trade_order  where account_id  > 1;