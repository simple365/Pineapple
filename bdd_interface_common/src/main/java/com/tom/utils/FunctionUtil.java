package com.tom.utils;

/**
 * 这个类用于处理${xxx()}内置的函数
 * @author Administrator
 *
 */
public class FunctionUtil {

	/**
	 * 获取时间戳，用于一些非重复类型
	 * @return
	 */
	public static String getTimeIdentity(){
		return System.currentTimeMillis()+"";
	}
	
}
