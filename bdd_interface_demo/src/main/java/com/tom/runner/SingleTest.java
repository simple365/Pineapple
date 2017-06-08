package com.tom.runner;

import org.testng.annotations.Test;

import com.tom.test.basic.SpecTestNgCuke;

import cucumber.api.CucumberOptions;

@Test
@CucumberOptions(features = { "src/main/resources/feature/business/basic.feature" }, glue = { "com.tom.test" }, plugin = {
		"pretty", "json:./target/cucumber/report.json","html:./target/cucumber/html" }, tags = {})
// @CucumberOptions(features = { "src/main/resources/feature/trustpay/transfer.feature:3" }, glue = { "com.tom.test" },
// plugin = {
// "pretty", "json:./target/cucumber/report.json" }, tags = {})
public class SingleTest extends SpecTestNgCuke {

}