package com.tom.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesUtil {

	public static String getProperty(String key, String PROPERTY_FILE) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(PROPERTY_FILE));
			props.load(in);
			in.close();
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 根据环境查看某个key是什么
	 * @param environment
	 * @param key
	 * @param PROPERTY_FILE
	 * @return
	 */
	public static String getPropertyByEnv(String key, String PROPERTY_FILE) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(PROPERTY_FILE));
			props.load(in);
			in.close();
			String value = props.getProperty(TestContext.getInstance().getFileHandle().getFileExt()+"."+key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setProperty(String key, String value, String PROPERTY_FILE) throws IOException {
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream(PROPERTY_FILE);
			prop.load(in);
			in.close();
			OutputStream out = new FileOutputStream(PROPERTY_FILE);
			prop.setProperty(key, value);
			prop.store(out, "Update '" + key + "' value");
			out.close();
		} catch (IOException e) {
			System.err.println("Visit " + PROPERTY_FILE + " for updating " + value + " value error.");
			throw e;
		}
	}
	
	public static Map<String, Object> getProperties(String PROPERTY_FILE) throws IOException{
		Map<String, Object> paras=null;
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream(PROPERTY_FILE);
			prop.load(in);
			in.close();
			paras=new HashMap<>();
			for(Entry<Object, Object> entry:prop.entrySet()){
				paras.put(String.format("${%s}",entry.getKey().toString()), entry.getValue());
			}
		} catch (IOException e) {
			throw e;
		}
		return paras;
	}

}
