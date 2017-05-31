package com.tom.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.jar.JarEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {
	com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
	Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	public JsonUtil() {
		super();
		objectMapper.getDeserializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 解析jsonObject
	 * 
	 * @param JsonString
	 * @return
	 */
	public JsonObject parseJsonObject(String JsonString) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(JsonString);
		return jsonElement.getAsJsonObject();
	}

	/**
	 * 解析JsonElement
	 * 
	 * @param JsonString
	 * @return
	 */
	public JsonElement parseJsonElement(String JsonString) {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(JsonString);
		return jsonElement;
	}

	public <T> T parseJsonString(String jsonString, Class<T> classType) {
		T t = null;
		try {
			if (classType == java.lang.String.class) {
				if(jsonString.isEmpty()){
					//空串解析报错
					t=(T)jsonString;
				}else{
				// 此处要特殊处理
				t=(T) jsonString.substring(jsonString.indexOf("\"") + 1, jsonString.lastIndexOf("\""));
				}
			} else {
				t = objectMapper.readValue(jsonString, classType);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(jsonString + " 转换成 " + classType + "失败");
			logger.error(e.getMessage());
		}
		return t;
	}

	public String objectToString(Object obje) {
		String string = null;
		try {
			string = objectMapper.writeValueAsString(obje);
			string = string.replace("\\\"", "\"").replace(":\"{",":{").replace("}\",","},");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return string;
	}
	
}
