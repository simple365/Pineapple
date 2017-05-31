package com.tom.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.tom.utils.handles.DubboHandle;
import com.tom.utils.handles.ExceptionHandle;
import com.tom.utils.handles.FileHandle;
import com.tom.utils.handles.MQProducer;
import com.tom.utils.handles.OutputHandle;
import com.tom.utils.handles.RSAHandle;

import ch.qos.logback.classic.Logger;

/**
 * 重要类，封装大部分参数在其中
 * 
 * @author Administrator
 *
 */
public class TestContext {
	private static Logger logger = (Logger) LoggerFactory.getLogger(TestContext.class);

	private static TestContext testContext = null;
//	private boolean internalScenario = false;
	Map<String, Object> scenarioParas = new HashMap<>();
	Map<String, Object> featureParas = new HashMap<>();
	Map<String, Object> globalParas = new HashMap<>();

	public final String LAST_RESPONSE_STRING = "last_response_string";
	private DubboHandle dubboHandle;
	private FileHandle fileHandle;
	private ExceptionHandle exceptionHandle;
	private MQProducer mqProducer;
	private OutputHandle outputHandle;
	private RSAHandle rsaHandle;

	private TestContext() {
	}

	public void init() throws Exception {
		dubboHandle = new DubboHandle();
		fileHandle = new FileHandle();
		exceptionHandle = new ExceptionHandle();
		rsaHandle=new RSAHandle();
		fileHandle.init();
		dubboHandle.init(fileHandle.getFileExt());
		this.outputHandle = new OutputHandle();
		setMqProducer(new MQProducer());
		rsaHandle.init();
		// 初始化全局参数
		try {
			putGlobalParameters(fileHandle.loadParaFile(Thread.currentThread().getContextClassLoader()
					.getResource("global/common." + fileHandle.getFileExt()).getPath()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("全局参数文件，gloal/common" + fileHandle.getFileExt()+ "不存在");
		}
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public static TestContext getInstance() {
		if (testContext == null) {
			try {
				testContext = new TestContext();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("没有找到 全局变量文件 \n" + e.getMessage());
			}
		}
		return testContext;
	}

	void setLastResult(Object content) {
		scenarioParas.put(LAST_RESPONSE_STRING, content);
	}

	/**
	 * 返回上一个http，dubbo请求的值
	 * 
	 * @return
	 */
	public Object getLastResult() {
		return scenarioParas.get(LAST_RESPONSE_STRING);
	}

	/**
	 * 返回上一个http，dubbo请求的值，以json字符串格式,这些有String有对象
	 * 
	 * @return
	 */
	public String getLastResultJson() {
		return new ParameterUtil().objectToParaString(scenarioParas.get(LAST_RESPONSE_STRING)).trim();
	}

	// ============scenario 参数开始
	public void putScenarioParameter(String key, Object value) {
		scenarioParas.put(key, value);
	}

	Object getScenarioParameter(String key) {
		return scenarioParas.get(key);
	}

	public void putScenarioParameters(Map<String, Object> paras) {
		this.scenarioParas.putAll(paras);
	}

	Map<String, Object> getScenarioParameters() {
		return scenarioParas;
	}

	/**
	 * 将保存的场景参数清空
	 */
	public void cleanScenarioParas() {
		scenarioParas.clear();
	}

	// ============feature 参数开始
	public void putFeatureParameter(String key, Object value) {
		featureParas.put(key, value);
	}

	Object getFeatureParameter(String key) {
		return featureParas.get(key);
	}

	public void putFeatureParameters(Map<String, Object> paras) {
		this.featureParas.putAll(paras);
	}

	Map<String, Object> getFeatureParameters() {
		return this.featureParas;
	}

	public void cleanFeatureParas() {
		featureParas.clear();
	}

	public void putGlobalParameter(String key, Object value) {
		globalParas.put(key, value);
	}

	public void putGlobalParameters(Map<String, Object> paras) {
		globalParas.putAll(paras);
	}

	Object getGlobalParameter(String key) {
		return globalParas.get(key);
	}

	public Object getParameter(String key) {
		if (scenarioParas.containsKey(key)) {
			return getScenarioParameter(key);
		} else if (featureParas.containsKey(key)) {
			return getFeatureParameter(key);
		} else if (globalParas.containsKey(key)) {
			return getGlobalParameter(key);
		} else {
			throw new RuntimeException(key + "该参数不存在");
		}
	}

	public Object getParameter(String key, Object defaultValue) {
		if (scenarioParas.containsKey(key)) {
			return getScenarioParameter(key);
		} else if (featureParas.containsKey(key)) {
			return getFeatureParameter(key);
		} else if (globalParas.containsKey(key)) {
			return getGlobalParameter(key);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 将保存的全局参数清空
	 */
	public void cleanTestParas() {
		globalParas.clear();
	}

	public DubboHandle getDubboHandle() {
		return dubboHandle;
	}

	public FileHandle getFileHandle() {
		return fileHandle;
	}

	public ExceptionHandle getExceptionHandle() {
		return exceptionHandle;
	}

	/**
	 * @return the mqProducer
	 */
	public MQProducer getMqProducer() {
		return mqProducer;
	}

	/**
	 * @param mqProducer
	 *            the mqProducer to set
	 */
	public void setMqProducer(MQProducer mqProducer) {
		this.mqProducer = mqProducer;
	}

	/**
	 * @return the outputHandle
	 */
	public OutputHandle getOutputHandle() {
		return outputHandle;
	}
	/**
	 * @return the outputHandle
	 */
	public RSAHandle getRsaHandle() {
		return rsaHandle;
	}

}
