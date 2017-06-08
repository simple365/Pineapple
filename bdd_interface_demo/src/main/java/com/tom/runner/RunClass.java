package com.tom.runner;

import org.testng.annotations.Test;

import com.tom.test.basic.SpecTestNgCuke;

import cucumber.api.CucumberOptions;

@Test
@CucumberOptions(features={"src/main/resources/feature/business/database.feature"},glue={"com.tom.test"},plugin = {"pretty", "html:./target/cucumber","json:./target/cucumber/report.json"})
public class RunClass extends SpecTestNgCuke {
	
	
}
