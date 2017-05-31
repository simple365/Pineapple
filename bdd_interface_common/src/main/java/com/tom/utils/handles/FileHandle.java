package com.tom.utils.handles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.utils.HookUtil;
import com.tom.utils.ParameterUtil;
import com.tom.utils.PropertiesUtil;
import com.tom.utils.TestConstants;
import com.tom.utils.TestContext;

import cucumber.api.Scenario;
import cucumber.api.testng.CucumberFeatureWrapper;

public class FileHandle {
	public static final String SCENARIO = "scenario";
	public static final String FEATURE = "feature";

	Logger logger = LoggerFactory.getLogger(FileHandle.class);
	public static final String PROPERTY_FILE = Thread.currentThread().getContextClassLoader().getResource(TestConstants.TEST_FILE)
			.getFile();
	private String fileDelimited;
	private String fileExt;
	// 参数分隔符
	public static final String FILE_DELIMITED = "fileDelimited";
	// 参数文件后缀
	public static final String FILE_EXT = "data.extension";

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		setFileDelimited(PropertiesUtil.getProperty(FILE_DELIMITED, PROPERTY_FILE));
		// jenkins中为环境变量，此处为默认
		String fileExt = System.getProperty(FILE_EXT);
		logger.info("maven 传入file ext 是:" + fileExt);
		if (fileExt == null)
			fileExt = PropertiesUtil.getProperty(FILE_EXT, PROPERTY_FILE);
		setFileExt(fileExt);
	}

	public String getFileDelimited() {
		return fileDelimited;
	}

	public void setFileDelimited(String fileDelimited) {
		this.fileDelimited = fileDelimited;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public Map<String, Object> loadParaFile(String fileName) {
		Map<String, Object> values = new HashMap<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				if (tmp.contains(fileDelimited)) {
					int lindex = tmp.lastIndexOf(fileDelimited);
					int index = tmp.indexOf(fileDelimited);
					String key = tmp.substring(0, index).trim();
					Object value = null;
					// if (index == lindex) {
					lindex = tmp.length();
					value = tmp.substring(index + 1).trim();
					// } else {
					// 指明了数据类型
					// value = new JsonUtil().parseJsonString(tmp.substring(index + 1, lindex + 1).trim(),
					// Class.forName(tmp.substring(lindex + 1).trim()));
					// }
					values.put("${" + key + "}", value);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return values;
	}

	public void packFeatureParas(String featurePath) {
		String fileName = featurePath.substring(0, featurePath.lastIndexOf("feature")) + fileExt;
		if (new File(fileName).exists()) {
			new ParameterUtil().addFeatureParameters(loadParaFile(fileName));
		}
	}

	public void cleanFeatureParas() {
		TestContext.getInstance().cleanFeatureParas();
	}

	public Scenario getCurrentScenario() {
		Object scenario = TestContext.getInstance().getParameter(SCENARIO,null);
		if (scenario == null)
			return null;
		return (Scenario) scenario;
	}

	public void setCurrentScenario(Scenario scenario) {
		TestContext.getInstance().putScenarioParameter(SCENARIO, scenario);
	}

	public CucumberFeatureWrapper getCurrentFeature() {
		Object feature = TestContext.getInstance().getParameter(FEATURE,null);
		if (feature == null)
			return null;
		return (CucumberFeatureWrapper) feature;
	}

	public void setCurrentFeature(CucumberFeatureWrapper cucumberFeatureWrapper) {
		TestContext.getInstance().putFeatureParameter(FEATURE, cucumberFeatureWrapper);
	}

	public String getValue(String key) throws Exception {
		return PropertiesUtil.getProperty(key, PROPERTY_FILE);
	}
}
