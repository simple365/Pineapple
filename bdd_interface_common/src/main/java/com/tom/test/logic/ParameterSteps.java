package com.tom.test.logic;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.test.basic.CucumberBusi;
import com.tom.test.basic.SpecCucumberFeature;
import com.tom.utils.Hex;
import com.tom.utils.HookUtil;
import com.tom.utils.ParameterUtil;
import com.tom.utils.RSAUtil;
import com.tom.utils.TestContext;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.FeatureResultListener;
import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Reflections;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.StopWatch;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;

public class ParameterSteps {
	Logger logger = LoggerFactory.getLogger(ParameterSteps.class);
	ParameterUtil paraUtil = new ParameterUtil();

	// 逻辑操作部分******************************************
	@And("^将当前时间保存为参数:(.+)格式:(.+)$")
	public void setTime(String para, String strFormat) {
		TestContext.getInstance().putScenarioParameter(para.trim(),
				new SimpleDateFormat(strFormat.trim()).format(Calendar.getInstance().getTime()));
	}

	@And("^将json返回结果:(.+) 保存为:(.+)$")
	public void saveJsonProperty(String path, String paraName) {
		paraUtil.setResultAsString(path, paraName);
	}

	// 参数部分**********************************
	/**
	 * 全局参数，则是任何scenario都可以用，必须手动清理
	 * 
	 * @param value
	 * @param name
	 */
	@And("^设置(.+)为全局参数(.+)$")
	public void setParameter(String value, String name) {
		ParameterUtil pUtil = paraUtil;
		value = pUtil.parseParameter(value);
		// object to string 时候会出现这个问题
		if (value.endsWith("\"") && value.startsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
		TestContext.getInstance().putGlobalParameter(name, value);
	}

	/**
	 * 用例集参数，则是feature内任何scenario都可以用，必须手动清理
	 * 
	 * @param value
	 * @param name
	 */
	@And("^设置(.+)为用例集参数(.+)$")
	public void setFeatureParameter(String value, String name) {
		ParameterUtil pUtil = paraUtil;
		value = pUtil.parseParameter(value);
		// object to string 时候会出现这个问题
		if (value.endsWith("\"") && value.startsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
		TestContext.getInstance().putFeatureParameter(name, value);
	}

	@And("^清空所有全局参数$")
	public void clearTest() {
		TestContext.getInstance().cleanTestParas();
	}

	/**
	 * 场景参数，在场景执行之后失效
	 * 
	 * @param value
	 * @param name
	 */
	@And("^设置(.+)为场景参数(.+)$")
	public void setParameterScenario(String value, String name) {
		ParameterUtil pUtil = paraUtil;
		value = pUtil.parseParameter(value);
		TestContext.getInstance().putScenarioParameter(name, value);
	}

	@And("^测试输出:(.+)$")
	public void testPrint(List<String> args) {
		// 单纯测试数据输出参数
		for (String string : args) {
			String temp = paraUtil.parseParameter(string);
			logger.info("输出参数" + string + ":" + temp);
			TestContext.getInstance().getFileHandle().getCurrentScenario().write("参数值" + string + "是:" + temp);
		}
	}

	@And("^将返回结果中:(.+)保存为场景参数:(.+),类型:(.+)$")
	public void saveResult(List<String> alias, List<String> paras, String paratype) {
		for (int i = 0; i < alias.size(); i++) {
			paraUtil.setResultAsObject(alias.get(i), paras.get(i), paratype);
		}
	}

	@And("^将返回结果中:(.+)保存为用例集参数:(.+),类型:(.+)$")
	public void saveFeatureResult(List<String> alias, List<String> paras, String paratype) {
		for (int i = 0; i < alias.size(); i++) {
			paraUtil.setResultObjectFeaturely(alias.get(i), paras.get(i), paratype);
		}
	}

	@And("^将返回结果中:(.+)保存为全局参数:(.+),类型:(.+)$")
	public void saveGlobalResult(List<String> alias, List<String> paras, String paratype) {
		for (int i = 0; i < alias.size(); i++) {
			paraUtil.setResultAsObject(alias.get(i), paras.get(i), paratype);
		}
	}

	@And("^将返回结果中:(.+)保存为场景参数:(.+)$")
	public void saveResultPara(List<String> alias, List<String> paras) {
		saveResult(alias, paras, "java.lang.String");
	}

	@And("^将返回结果中:(.+)保存为用例集参数:(.+)$")
	public void saveResultParaFeature(List<String> alias, List<String> paras) {
		saveFeatureResult(alias, paras, "java.lang.String");
	}

	@Then("^验证返回结果中存在:(.*)$")
	public void lastContains(List<String> content) {
		String lastResult = TestContext.getInstance().getLastResultJson();
		for (int i = 0; i < content.size(); i++) {
			String conts = paraUtil.parseParameter(content.get(i)).trim();
			if (!lastResult.contains(conts)) {
				throw new RuntimeException(conts + " 无法在返回内容中找到。");
			}
		}
	}

	@Then("^验证返回结果等于:(.+)$")
	public void lastEquals(String content) {
		String lastResult = TestContext.getInstance().getLastResultJson().trim();
		String conts = paraUtil.parseParameter(content).trim();
		if (!lastResult.equals(conts)) {
			throw new RuntimeException(content + " 与返回内容不相等。");
		}
	}

	@Then("^验证参数:(.+?)等于:(.+)$")
	public void paraEquals(String para, String content) {
		String value = paraUtil.getParaInString(para).trim();
		String conts = paraUtil.parseParameter(content).trim();
		if (!value.equals(conts)) {
			throw new RuntimeException(content + " 与参数" + para + "内容:" + value + "不相等。");
		}
	}

	@Then("^验证参数:(.+?)中存在:(.+)$")
	public void paracontains(String para, String contentStr) {
		String value = paraUtil.getParaInString(para).trim();
		if (contentStr.contains("或")) {
			boolean flag = false;
			String[] contentGroup = contentStr.split("或");
			for (int j = 0; j < contentGroup.length; j++) {
				String[] content = contentGroup[j].split(",");
				int count=0;
				for (int i = 0; i < content.length; i++) {
					String conts = paraUtil.parseParameter(content[i]).trim();
					if (value.contains(conts)) {
					   count++;
					}
				}
				if(count==content.length){
					flag=true;
				}
			}
			if(!flag){
				throw new RuntimeException("参数中无法找到相应的值");
			}
		} else {
			String[] content = contentStr.split(",");
			for (int i = 0; i < content.length; i++) {
				String conts = paraUtil.parseParameter(content[i]).trim();
				if (!value.contains(conts)) {
					throw new RuntimeException(conts + " 在参数" + para + "内容中:" + value + "不存在。");
				}
			}
		}
	}

	// **********参数运算
	@And("^验证(.+)加(.+)等于(.+)$")
	public void paraPlus(String para, String para2, String expected) {
		int a = Integer.parseInt(paraUtil.parseParameter(para));
		int b = Integer.parseInt(paraUtil.parseParameter(para2));
		int c = Integer.parseInt(paraUtil.parseParameter(expected));
		if ((a + b) != c) {
			throw new RuntimeException(a + " 加" + b + "不等于:" + c);
		}
	}

	@And("^字符串:(.+)拼接:(.+)保存为场景参数:(.+)$")
	public void contactTwoString(String a, String b, String result) {
		ParameterUtil pUtil = paraUtil;
		String value_a = pUtil.parseParameter(a);
		String value_b = pUtil.parseParameter(b);
		String value = value_a + value_b;
		TestContext.getInstance().putScenarioParameter(result, value);
	}

	@And("^执行feature:(.+?),tags=(.*)$")
	public void invokeScenario(String featurePath, String tags) {
		// 设置为内部执行的情况，跳过before 和 after 处理
//		TestContext.getInstance().setInternalScenario(true);
		execScenario(featurePath, tags);
//		TestContext.getInstance().setInternalScenario(false);
	}

	private void execScenario(String featurePath, String tags) {
		if (!tags.isEmpty()) {
			tags = String.format("--tags '%s'", tags);
		}

		RuntimeOptions runtimeOptions = new RuntimeOptions(String.format("%s --glue 'com.tom.test' %s", featurePath, tags));
		Runtime runtime;
		ResourceLoader resourceLoader;
		FeatureResultListener resultListener;
		ClassLoader classLoader;
		classLoader = CucumberBusi.class.getClassLoader();
		resourceLoader = new MultiLoader(classLoader);
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
		resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
		// 获取自己的stopwatch, 为system传入
		Reflections reflections = new Reflections(classFinder);
		Collection<? extends Backend> backends = reflections.instantiateSubclasses(Backend.class, "cucumber.runtime",
				new Class[] { ResourceLoader.class }, new Object[] { resourceLoader });
		StopWatch SYSTEM = new StopWatch() {
			private final ThreadLocal<Long> start = new ThreadLocal<Long>();

			@Override
			public void start() {
				start.set(System.nanoTime());
			}

			@Override
			public long stop() {
				Long duration = System.nanoTime() - start.get();
				start.set(null);
				return duration;
			}
		};
		runtime = new Runtime(resourceLoader, classLoader, backends, runtimeOptions, SYSTEM, null);

		List<CucumberFeature> features = SpecCucumberFeature.load(resourceLoader, runtimeOptions.getFeaturePaths(),
				runtimeOptions.getFilters());
		HookUtil hookUtil = new HookUtil();
		for (int i = 0; i < features.size(); i++) {
			resultListener.startFeature();
			CucumberFeature cucumberFeature = features.get(i);
			hookUtil.beforeFeature(new CucumberFeatureWrapper(cucumberFeature));
			cucumberFeature.run(runtimeOptions.formatter(classLoader), resultListener, runtime);
			if (!resultListener.isPassed()) {
				throw new CucumberException(resultListener.getFirstError());
			}
		}

	}

	// ************************************************************
	@And("^等待:(\\d+)秒$")
	public void waitTime(int mili) throws InterruptedException {
		Thread.sleep(mili * 1000);
	}

	@And("^将参数:(.+?)添加进properties文件$")
	public void saveToJson(List<String> parameters) {
		ParameterUtil pUtil = paraUtil;
		for (String parameter : parameters) {
			String value = pUtil.parseParameter(parameter);
			TestContext.getInstance().getOutputHandle().writePara(parameter, value);
		}
	}

	@And("参数:(.+?)反转RSA加密成场景参数:(.+)")
	public void encryptParas(List<String> parameters,List<String> names) throws Exception{
//		RSAPublicKey rsap = (RSAPublicKey) RSAUtil.generateKeyPair().getPublic();
		PublicKey publicKey=RSAUtil.getKeyPair().getPublic();
		for (int i=0;i<parameters.size();i++) {
			String parameter=parameters.get(i);
			String value = paraUtil.parseParameter(parameter);
			value=new StringBuilder(value).reverse().toString();
			byte[] en_test = RSAUtil.encrypt(publicKey, value.getBytes());
			TestContext.getInstance().putScenarioParameter(names.get(i).trim(), Hex.encodeHexStr(en_test));
		}
	}
}
