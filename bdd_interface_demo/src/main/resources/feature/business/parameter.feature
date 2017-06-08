Feature: 参数域演示

@test1
Scenario:测试保存用例集参数
    Given post请求接口:http://172.16.23.30/serverInfo/search?page=1&size=2,{"page":"1","size":"2"}
    And 将返回结果中:.保存为用例集参数:${abc}

@test1
Scenario:测试输出
	Then 测试输出:${abc}
	And 等待:4秒
	Then 测试输出:${abc}
	
@test2
Scenario:测试环境切换
	Then 测试输出:${username}
  