Feature: 数据演示 

@test1 
Scenario: 数据生成随机数 
	Given 生成随机身份证号,参数名:${id} 
	And 生成随机手机号,参数名:${mobile}
	Then 测试输出:${id},${mobile}