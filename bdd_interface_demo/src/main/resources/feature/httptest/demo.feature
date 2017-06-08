Feature: http演示用

  @test @冒烟
  Scenario: 测试调用自定义函数
    And 设置${com.tom.utils.TaUtils.getBeforeDay(int=1)}为场景参数${out_tradeNo}
    Then 测试输出:${out_tradeNo}

  @testapi
  Scenario: 用例中存在头
    Given 添加场景http头:Connection:keep-alive
    And 测试输出:${flag}
    Given post请求接口:http://10.166.224.98:8181/api/account/signin,{"name":"${username}","password":"${password}","remember":0}

  @test
  Scenario: 测试open-id登录
    #	Given 添加场景http头:Origin:http://106.2.97.87:8184,Referer:http://106.2.97.87:8184/login
    Given post请求接口:http://10.166.224.98:8181/j_spring_security_check,{"j_username":"admin","j_password":"test","submit":"登 录"}
    And get请求链接:http://10.166.224.98:8181/login

  @test3
  Scenario: 测试调用feature
    Given 设置123213为用例集参数${abc}
    And 执行feature:src/main/resources/feature/business/parameter.feature,tags=@test2

