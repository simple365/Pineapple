Feature: 用于演示cucumber的基本特性
包括feature，scenario，steps，tag，datatable,background。
 
Background: 每次用例执行之前会执行
When 测试输出:広い広いこの空より。もっと広いはずた

@test1 
Scenario: 单一的一个用例
    Given 测试输出:其实我是一个用例
    
@test2
Scenario Outline: 一个用例执行不同的数据
    Given 输入值 <a>,<b>
    And 计算两个值相乘
    Then 验证结果等于 2

    Examples:
    |a|b|
    |1|2|
    |100|56|