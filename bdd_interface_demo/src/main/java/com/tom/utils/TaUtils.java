package com.tom.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaUtils {

	
	public static String getBeforeDay(int number){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -number);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		System.out.println(yesterday);
		return yesterday;
	}
	
	public static String getDay(int number){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, +number);
		
		String day = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		return day;
	}
	
	public static String sleepSomeTime(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "sleep"+String.valueOf(seconds)+" seconds.";
	}
	public static String getCurrentDateTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
		System.out.println(df.format(new Date()));
		return df.format(new Date());
	}
	
	public static void main(String[] args) {
		TaUtils.getCurrentDateTime();
		
	}
}
