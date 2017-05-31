package com.tom.test.logic;

import com.tom.utils.RandomUtil;
import com.tom.utils.TestContext;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

/**
 * @author Luo Shengjie
 *
 */
public class DataSteps {

	@Then("^生成随机身份证号,参数名:(.*)$")
	public void dExceptionContains(String para) throws Exception {
		String value = RandomUtil.getRandomID();
		TestContext.getInstance().putScenarioParameter(para, value);
	}

	@And("^生成随机手机号,参数名:(.*)$")
	public void randMobile(String para) throws Exception {
		String value = RandomUtil.getTel();
		TestContext.getInstance().putScenarioParameter(para, value);
	}

}
