Feature: Dubbo的调用demo
  
  @test1
  Scenario: dubbo测试
  Given Dubbo调用:com.tom.ITrustProjectConfigService.getAllProjectTypes,参数:
  And 将返回结果中:00 保存为场景参数:${k}
  And 测试输出:${k}
  # 1. 标准调用，使用 | 分隔
  #Given Dubbo调用:{"接口名":"com.tom.IProductService","方法名":"queryByPage","参数":{"int":1,"int":25,"java.lang.String":null,"java.lang.String":null}}|
  
  # 2. 参数写成一行一行的
    #Given Dubbo调用:com.tom.IProductService.queryByID,参数内容:
      #"""
         #java.lang.String:"SCFAE|A080015000001" 
         #com.tom.Product:{"id":"SCFAEQY15100870","name":"find","prices":[12,23,1]}
      #"""
  
  # 3. 参数写成列表格式的
  #Given Dubbo调用:com.tom.IProductService.queryByID,参数列表:
  #| 类型               | 值                    |
  #| java.lang.String | "SCFAEA080015000001" |
  #And 将返回结果中:.保存为场景参数:${k}
  #And 将返回结果中:class保存为场景参数:${b}
  #And 将返回结果中:class保存为场景参数:${h}
  #And 验证参数:${k}中存在:${b}
  #And 验证参数:${h}等于:${b}
  #And 测试输出:${k}
  #And 验证返回结果等于:null
  
  @测试
  Scenario: dubbo测试2
   #  传null示例
   Given Dubbo调用:com.tom.IProductService.queryOnSaleByPage,参数:int:1|int:25|java.lang.String:"null"|
  #  枚举示例
  #Given Dubbo调用:com.tom.IProductService.updateState,参数:java.lang.String:"SCFAEQY15100666"|com.tom.ProductStatus:PAUSE|
  #  自定义对
  #Given Dubbo调用:com.tom.IProductService.countByCondition,参数:com.tom.Product:{"id":"SCFAEQY15100870"}
  #  不传参数
  #Given Dubbo调用:com.tom.IResolveJobService.resolve,参数:
  #  Amount为Long型
  #Given Dubbo调用:com.tom.IOrderService.submitOrder,参数:com.tom.Order:{"UserNo":"001","Mobile":"13508888888"}|
  #  对象属性是时间
  #Given Dubbo调用:com.tom.IPaymentService.refund,参数:java.util.List:[{"BankCardNo":"6224423560042538","ProductNo":"SCFAEQY14001010"}]|java.lang.String:"product"|
