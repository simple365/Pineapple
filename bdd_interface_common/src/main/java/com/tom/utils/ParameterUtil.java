package com.tom.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.dubbo.common.compiler.support.ClassUtils;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParameterUtil {

	public void cleanScenarioParas() {
		TestContext.getInstance().cleanScenarioParas();
	}

	/**
	 * 将结果保存为string
	 * @param alias
	 * @param parameterName
	 */
	public void setResultAsString(String alias, String parameterName) {
		String[] indexes = alias.split("\\.");
		JsonParser jsonParser = new JsonParser();
		String jsonStr = TestContext.getInstance().getLastResultJson();
		if (jsonStr.startsWith("\"")) {
			jsonStr = jsonStr.substring(1);
		}
		if (jsonStr.endsWith("\"")) {
			jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
		}
		JsonElement jsonElement = null;
		try {
			jsonElement = jsonParser.parse(jsonStr);
		} catch (Exception e) {
			throw new RuntimeException(" 不是标准的json字符串" + jsonStr.substring(0, 500 > jsonStr.length() ? jsonStr.length() : 500));
		}
		JsonElement value = jsonElement;
		for (int i = 0; i < indexes.length; i++) {
			String index = indexes[i];
			// 数组
			if (index.startsWith(TestConstants.INDEX_PREFIX)) {
				value = value.getAsJsonArray().get(Integer.parseInt(index.replace("_", "")));
				// value=value.getasjson
			} else {
				// json
				value = value.getAsJsonObject().get(index);
			}
		}
		String result = null;
		if (value == null) {
			throw new RuntimeException(alias + "无法在返回结果中找到");
		}
		if (value instanceof JsonObject) {
			result = value.getAsJsonObject().toString();
		} else if (value instanceof JsonArray) {
			result = value.getAsJsonArray().toString();
		} else if (value instanceof JsonNull) {
			result = "null";
		} else {
			result = value.getAsString().trim();
		}
		TestContext.getInstance().putScenarioParameter(parameterName, result);
	}

	public <T> void setResultAsObject(String alias, String parameterName, String saveType) {
		try {
			alias = parseParameter(alias);
			Object object = new JsonUtil().parseJsonString(lastResultJsonString(alias, saveType), Class.forName(saveType));
			TestContext.getInstance().putScenarioParameter(parameterName, object);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> void setResultObjectGlobally(String alias, String parameterName, String saveType) {

		try {
			alias = parseParameter(alias);
			Object object = new JsonUtil().parseJsonString(lastResultJsonString(alias, saveType), Class.forName(saveType));
			TestContext.getInstance().putGlobalParameter(parameterName, object);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> void setResultObjectFeaturely(String alias, String parameterName, String saveType) {
		try {
			alias = parseParameter(alias);
			Object object = new JsonUtil().parseJsonString(lastResultJsonString(alias, saveType), Class.forName(saveType));
			TestContext.getInstance().putFeatureParameter(parameterName, object);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * alias是路径，将值拿到，并且换成json string
	 * 
	 * @param alias
	 * @param parameterName
	 * @param saveType
	 * @return
	 */
	public String lastResultJsonString(String alias, String saveType) {
		if(StringUtils.isBlank(alias)){
			alias=".";
		}
		String[] indexes = alias.split("\\.");
		String result = "";
		if (indexes.length == 0 && saveType.contains("java.lang.String")) {
			return "\"" + TestContext.getInstance().getLastResultJson() + "\"";
		} else {
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(TestContext.getInstance().getLastResultJson());
			JsonElement value = jsonElement;
			for (int i = 0; i < indexes.length; i++) {
				String index = indexes[i];
				// 数组
				if (index.startsWith("_")) {
					value = value.getAsJsonArray().get(Integer.parseInt(index.replace("_", "")));
					// value=value.getasjson
				} else {
					// json
					value = value.getAsJsonObject().get(index);
				}
			}
			if (value == null) {
				throw new RuntimeException(alias + "无法在返回结果中找到");
			}
			if (value instanceof JsonObject || value instanceof JsonArray) {
				result = value.getAsJsonObject().toString();
			} else if (value instanceof JsonArray) {
				result = value.getAsJsonArray().toString();
			} else if (value instanceof JsonNull) {
				result = "null";
			} else {
				result = value.getAsString().trim();
			}
			return new JsonUtil().objectToString(result);
		}
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 将字符串中含有 ${}或 $()的替换成值,分三种情况，一种是${}完全的，一种是在中间，还有$()这种，统一变成 json字符串
	 * 
	 * @param toParse
	 * @return 对象返回json格式，简单类型直接返回
	 */
	public String parseParameter(String toParse) {
		String result = toParse.trim();
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(toParse);
		while (matcher.find()) {
			String para = matcher.group();
			// 判断方法
			String pname = matcher.group(1);
			String value = null;
			if (pname.contains("(") && pname.contains(")")) {
				Pattern methodPattern = Pattern.compile("(.+?)\\((.*?)\\)");
				Matcher methodMatcher = methodPattern.matcher(pname.trim());
				if (methodMatcher.find()) {
					// 先不考虑传参数的情况，
					String methodName = methodMatcher.group(1);
					String orValues=methodMatcher.group(2).trim();
					String[] keyValues=orValues.split("\\|");
					if(keyValues[0].isEmpty()){
						keyValues=new String[0];
					}
					String className = null;
					Class clazz = null;
					try {
						if (!methodName.contains(".")) {
							clazz = FunctionUtil.class;
						} else {
							// 包名 com.tom.test.Function.test()
							className = methodName.substring(0, methodName.lastIndexOf("."));
							clazz = Class.forName(className);
						}
						methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
						List<Class> types=new ArrayList<>();
						List<Object> values=new ArrayList<>();
						for(int i=0;i<keyValues.length;i++){
							String[] kv=keyValues[i].split("=");
							types.add(ClassUtils._forName(kv[0]));
							values.add(JSON.parse(kv[1], ClassUtils._forName(kv[0])));
						}
						Method method = clazz.getMethod(methodName, types.toArray(new Class[types.size()]));
						value=method.invoke(clazz.newInstance(), values.toArray()).toString();
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("方法:" + pname + " 无法找到");
					} catch (InstantiationException e) {
						throw new RuntimeException("类:" + className + " 构造失败");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				Object obj = TestContext.getInstance().getParameter(para);
				if (obj instanceof String) {
					value = obj.toString();
				} else {
					value = new JsonUtil().objectToString(obj);
				}
			}
			result = result.replace(para, value);
		}

		return result;
	}

	/**
	 * 将json转化成map，如果中间有null，则直接保存null
	 * 
	 * @param json
	 * @return
	 */
	public Map<String, String> packJson2Map(String json) {
		Map<String, String> data = new HashMap<String, String>();
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = null;
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		for (Entry<String, JsonElement> elEntry : jsonObject.entrySet()) {
			jsonElement = elEntry.getValue();
			if (jsonElement == JsonNull.INSTANCE) {
				data.put(elEntry.getKey(), null);
			} else if (jsonElement instanceof JsonArray) {
				data.put(elEntry.getKey(), jsonElement.toString());
			} else {
				data.put(elEntry.getKey(), jsonElement.getAsString());
			}
		}
		return data;
	}

	/**
	 * 
	 * @param para
	 *            参数的名字${}
	 * @return
	 */
	public Object getParameter(String para) {
		return TestContext.getInstance().getParameter(para);
	}
	public Object getParameter(String para,Object defaultValue) {
		return TestContext.getInstance().getParameter(para,defaultValue);
	}

	/**
	 * 获取参数的内容，以项目约定的形式，string不会加上引号
	 * 
	 * @param para
	 * @return
	 */
	public String getParaInString(String para) {
		return objectToParaString(TestContext.getInstance().getParameter(para));
	}

	/**
	 * 将参数保存为 string 类型
	 * 
	 * @param key
	 * @param value
	 */
	public void saveScenarioParaString(String key, String value) {
		TestContext.getInstance().putScenarioParameter(key, value);
	}

	/**
	 * 该方法与Jsonutil不同，不会在string外面添加引号
	 * 
	 * @param object
	 * @return
	 */
	public String objectToParaString(Object object) {
		if (object instanceof String) {
			return object.toString();
		} else {
			return new JsonUtil().objectToString(object);
		}
	}

	/**
	 * 这个方法在每个请求，dubbo请求等等之后执行，保存返回的值
	 */
	public void saveResult(Object content) {
		TestContext.getInstance().setLastResult(content);
	}

	/**
	 * 添加新的用例集参数，如果发生冲突，以新的为准
	 * @param paras
	 */
	public void putFeatureParameter(String name, Object value){
		TestContext.getInstance().putFeatureParameter(name, value);
	}
	/**
	 * 添加新的用例集参数，如果发生冲突，以新的为准
	 * @param paras
	 */
	public void putScenarioParameter(String name, Object value){
		TestContext.getInstance().putScenarioParameter(name, value);
	}
	/**
	 * 添加新的用例集参数，如果发生冲突，以老的为准
	 * @param paras
	 */
	public void addFeatureParameters(Map<String, Object> paras){
		paras.putAll(TestContext.getInstance().getFeatureParameters());
		TestContext.getInstance().putFeatureParameters(paras);
	}
	
	/**
	 * 添加新的场景参数集，如果发生冲突，以老的为准
	 * @param paras
	 */
	public void addScenarioParameters(Map<String, Object> paras){
		paras.putAll(TestContext.getInstance().getScenarioParameters());
		TestContext.getInstance().putScenarioParameters(paras);
	}
}
