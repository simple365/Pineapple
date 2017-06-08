package com.tom.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.testng.annotations.Test;


/**
 * 该类用于Dubbo 语句生成
 * 
 * @author Administrator
 *
 */
@Test(groups="tool",enabled=false)
public class DubboGenerator {
	@Test
	public void generateDubboString() {
//		Class class1 = IAppInfoService.class;
		BufferedReader bf = null;
		StringBuilder sb = new StringBuilder();
		try {
			File file = org.springframework.util.ResourceUtils.getFile("classpath:test/method.txt");
			bf = new BufferedReader(new FileReader(file));
			String content = "";
			while (content != null) {
				content = bf.readLine();
				if (content == null) {
					break;
				}
				sb.append(content.trim());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		String fullName = class1.getName();
//		for(Method method:class1.getMethods()){
//			String methodName=method.getName();
//			System.out.println(String.format(sb.toString(), class1.getSimpleName(),methodName,methodName+"test",fullName,methodName));
//		}
	}
		
		@Test
		public void generateFeatureString() {
			//设置要调用的service

			BufferedReader bf = null;
			StringBuilder sb = new StringBuilder();
			try {
				File file = org.springframework.util.ResourceUtils.getFile("classpath:test/feature.txt");
				bf = new BufferedReader(new FileReader(file));
				String content = "";
				while (content != null) {
					content = bf.readLine();
					if (content == null) {
						break;
					}
					sb.append(content.trim());
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					bf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			String fullName = class1.getName();
//			for(Method method:class1.getMethods()){
//				String methodName=method.getName();
//				StringBuilder para=new StringBuilder();
//				for(Class paraType:method.getParameterTypes()){
//					para.append(String.format("\"%s\":null",paraType.getName()));
//				}
//				System.out.println(String.format(sb.toString(), fullName, methodName,para.toString()));
//			}
	}
}
