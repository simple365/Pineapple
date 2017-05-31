package com.tom.test.basic;

import com.tom.utils.HookUtil;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class CucumberBusi {
	private HookUtil hookUtil;
	
	public CucumberBusi() {
		// TODO Auto-generated constructor stub
		hookUtil=new HookUtil();
	}
	
	@After(order=999999)
	public void afterScenario(Scenario scenario){
		hookUtil.afterScenario(scenario);
	}
	
	
	@Before(order=999999)
	public void beforeScenario(Scenario scenario){
		hookUtil.beforeScenario(scenario);
	}
}
