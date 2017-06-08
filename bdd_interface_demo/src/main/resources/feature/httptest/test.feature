Feature: hey it is you 

@开放平台 @zhu
Scenario:测试访问https
  Then get请求链接:https://api.github.com/users/bulkan
	And 设置123为用例集参数${abc}
	Then ddb连接,验证返回结果条数等于1:select * from purchase_trade_order limit 1
	And  ddb执行:select trade_no,status from purchase_trade_order limit 1结果保存为场景参数:${a},${b}
	Then 测试输出:${a},${b}
  Then 将参数:${a},${b}添加进properties文件
  And 验证参数:${a}中存在:D或P
	And 验证返回结果中存在:足球

