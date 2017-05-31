package com.tom.test.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tom.utils.HookUtil;
import com.tom.utils.TestContext;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

public class SpecTestNgCuke {
	private SpecCucumberRunner testNGCucumberRunner;
	private File jsonPath=null;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() throws Exception {
		TestContext.getInstance().init();
		Class<? extends SpecTestNgCuke> clazz=this.getClass();
		testNGCucumberRunner = new SpecCucumberRunner(clazz);
		//拿注解
		CucumberOptions options=clazz.getAnnotation(CucumberOptions.class);
		for(String plugin:options.plugin()){
			 //判断是json
			if(plugin.trim().startsWith("json")){
				jsonPath=new File(plugin.split(":")[1]);
			}
		}
	}

	@Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
	public void feature(CucumberFeatureWrapper cucumberFeature) {
		HookUtil hookUtil = new HookUtil();
		hookUtil.beforeFeature(cucumberFeature);
		try {
			testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			hookUtil.afterFeature(cucumberFeature);
		}
	}

	/**
	 * @return returns two dimensional array of {@link CucumberFeatureWrapper}
	 *         objects.
	 */
	@DataProvider
	public Object[][] features() {
		return testNGCucumberRunner.provideFeatures();
	}

	@AfterClass
	public void tearDownClass() throws Exception {
		testNGCucumberRunner.finish();
		//生成报告
		File reportOutputDirectory = new File("target/cucumber");
		List<String> jsonFiles = new ArrayList<>();
		jsonFiles.add(jsonPath.getAbsolutePath());

		String projectName = "cuke";

		Configuration configuration = new Configuration(reportOutputDirectory, projectName);

		ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
		reportBuilder.generateReports();
	}

}
