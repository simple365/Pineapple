package com.tom.utils.handles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.tom.utils.JsonUtil;
import com.tom.utils.LogTools;
import com.tom.utils.ParameterUtil;
import com.tom.utils.PropertiesUtil;
import com.tom.utils.TestContext;
import com.tom.utils.dto.DubboIdentityDto;

public class DubboHandle {
	Logger logger = LoggerFactory.getLogger(DubboHandle.class);
	private static final String PROPERTY_FILE = Thread.currentThread().getContextClassLoader()
			.getResource("dubbo.properties").getPath();

	public static final String DEFAULT_DELIMIED = "\\|";

	private ReferenceConfig<GenericService> reference;
	String fileExt = null;

	public void init(String fileExt) {
		this.fileExt = fileExt;
	}

	public DubboIdentityDto parseDubboParams(String argString) {
		return parseDubboParams(argString, DEFAULT_DELIMIED);
	}

	public DubboIdentityDto parseDubboParams(String argString, String delimited) {
		DubboIdentityDto dubboIdentityDto = new DubboIdentityDto();
		// 参数类型列表
		List<String> paratypes = new ArrayList<>();
		List<Object> argValues = new ArrayList<>();
		String[] kvs = null;
		kvs = argString.split(delimited);
		for (int i = 0; i < kvs.length; i++) {
			if (kvs[i].isEmpty())
				continue;
			String key = kvs[i].substring(0, kvs[i].indexOf(":")).trim();
			paratypes.add(key);
			String value = kvs[i].substring(kvs[i].indexOf(":") + 1).trim();
			Object vas = null;
			vas = parseValue(key, value);
			argValues.add(vas);
		}
		// 设置参数
		dubboIdentityDto.setParameterTypes(paratypes.toArray(new String[paratypes.size()]));
		dubboIdentityDto.setArgs(argValues.toArray());
		return dubboIdentityDto;
	}

	/**
	 * dubbo 规范是，类型不用加引号，值的话，string,date和属性名必须要加引号
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object parseValue(String key, String value) {
		Object vas = null;
		if (!"null".equals(value)) {
			if (key.endsWith("String")) {
				vas = value.substring(1, value.length() - 1);
			} else if (key.endsWith("Date")) {
				// 只考虑Java.Util.Date类型
				try {
					vas = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("日期格式不正确", e);
				}
			} else if (value.startsWith("{") || value.startsWith("[")) {
				// 这种情况为对象类型
				try {
					vas = JSON.parse(value, Object.class);
				} catch (com.alibaba.dubbo.common.json.ParseException e) {
					// TODO Auto-generated catch block
					logger.error("Dubbo json 格式错误" + value);
				}
			} else {
				vas = value;
			}
		}
		return vas;
	}

	public <T> T dubboExecute(DubboIdentityDto dto) {
		T result = null;
		// 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
		reference = new ReferenceConfig<GenericService>();
		// 分组调用
		reference.setGroup(PropertiesUtil.getProperty(fileExt + ".dubbo.service.group_name", PROPERTY_FILE));
		// 超时毫秒
		reference.setTimeout(60000);

		// uid-业务需求
		RpcContext context = RpcContext.getContext();
		context.setAttachment(LogTools.UID, LogTools.generateUID());

		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(PropertiesUtil.getProperty(fileExt + ".dubbo.application.name", PROPERTY_FILE));
		applicationConfig.setOwner(PropertiesUtil.getProperty(fileExt + ".dubbo.application.owner", PROPERTY_FILE));
		reference.setApplication(applicationConfig);
		RegistryConfig config = new RegistryConfig(
				PropertiesUtil.getProperty(fileExt + ".dubbo.registry.address", PROPERTY_FILE));
		reference.setRegistry(config);
		reference.setInterface(dto.getInterfaceName());// 弱类型接口名
		MonitorConfig monitorConfig = new MonitorConfig();
		monitorConfig.setProtocol(PropertiesUtil.getProperty(fileExt + ".dubbo.protocol.port", PROPERTY_FILE));
		reference.setMonitor(monitorConfig);
		// reference.setVersion(operDef.getVersion());
		reference.setAsync(false);// 异步调用

		// 声明为泛化接口
		reference.setGeneric(true);
		// 缓冲起来
		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
		try {
			GenericService genericService = cache.get(reference);
			logger.info("Dubbo调用" + dto.getInterfaceName() + "." + dto.getMethodName());
			logger.info("Dubbo传入参数类型" + new JsonUtil().objectToString(dto.getParameterTypes()) + "长度"
					+ dto.getParameterTypes().length);
			logger.info("Dubbo传入参数" + new JsonUtil().objectToString(dto.getArgs()) + "长度" + dto.getArgs().length);
			// 基本类型以及Date,List,Map等不需要转换，直接调用
			result = (T) genericService.$invoke(dto.getMethodName(), dto.getParameterTypes(), dto.getArgs());
		} catch (Exception e) {
			// 保存dubbo异常消息
			String messge = TestContext.getInstance().getExceptionHandle().setDubboException(e);
			logger.error(messge);
			// 必须清除缓存，否则下次会获取到空指针
			cache.destroy(reference);
			// 需要处理消息执行失败
			// throw e;
		}
		logger.info("Dubbo调用返回结果 " + new JsonUtil().objectToString(result));
		// Dubbo的返回结果保存
		new ParameterUtil().saveResult(result);
		return result;
	}
}
