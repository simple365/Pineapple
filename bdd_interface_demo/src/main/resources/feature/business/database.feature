Feature: 数据库演示

@test1
Scenario:数据库执行，返回结果保存为参数
    #数据库执行，返回结果保存为参数
    And mysql执行:select id,task_name from task_log limit 1 结果保存为场景参数:${a},${b}
    Then 测试输出:${a},${b}
		And oracle连接:jdbc:oracle:thin:@127.0.0.1:1521:zsytest用户名:test密码:test
		Then oracle连接,验证返回结果条数等于1:select * from book where rownum=1

@test2
Scenario:测试数据库连接,第二个用例
    Given ddb验证结果不等于null,1:select bank_code,card_lenght from cards where bank_code='ABC' limit 1
		Then oracle连接,验证返回结果条数等于1:select * from t_test
	
@test3
Scenario:测试数据
   Given ddb连接,验证返回结果等于${r1},1:select bank_code,status from bank_base where bank_code='ABC'