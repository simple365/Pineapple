package com.tom.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.test.basic.SpecCucumberFeature;
import com.tom.utils.factory.DBFactory;

import cucumber.api.Scenario;
import cucumber.api.testng.CucumberFeatureWrapper;

public class HookUtil {
	Logger logger = LoggerFactory.getLogger(HookUtil.class);
	private ParameterUtil parameterUtil;

	public HookUtil() {
		// TODO Auto-generated constructor stub
		parameterUtil = new ParameterUtil();
	}

	public void beforeScenario(Scenario scenario) {
		// 判断是否是内部调用的scenario
		if (TestContext.getInstance().getFileHandle().getCurrentScenario() == null) {
			TestContext.getInstance().getFileHandle().setCurrentScenario(scenario);
		}
	}

	public void afterScenario(Scenario scenario) {
		if (TestContext.getInstance().getFileHandle().getCurrentScenario().equals(scenario)) {
			new HttpUtil().closeSession();
			DBFactory.closeAll();
			// 如果发生链接修改，自动清除所有的数据库对象
			DBFactory.cleanse();
			parameterUtil.cleanScenarioParas();
			// 关闭mq
			TestContext.getInstance().getMqProducer().destory();
		}
	}

	public void beforeFeature(CucumberFeatureWrapper cucumberFeatureWrapper) {
		if (TestContext.getInstance().getFileHandle().getCurrentFeature() == null) {
			TestContext.getInstance().getFileHandle().setCurrentFeature(cucumberFeatureWrapper);
		}
		String path = ((SpecCucumberFeature) cucumberFeatureWrapper.getCucumberFeature()).getAbsolutePath();
		TestContext.getInstance().getFileHandle().packFeatureParas(path);
		logger.info(cucumberFeatureWrapper + "路径:" + path + "解读feature参数:"
				+ TestContext.getInstance().getFeatureParameters().toString());
	}

	public void afterFeature(CucumberFeatureWrapper cucumberFeatureWrapper) {
		if (TestContext.getInstance().getFileHandle().getCurrentFeature().equals(cucumberFeatureWrapper)) {
			TestContext.getInstance().getFileHandle().cleanFeatureParas();
			logger.info(cucumberFeatureWrapper + "销毁feature参数:" + TestContext.getInstance().getFeatureParameters().toString());
		}
	}
}
