package com.tom.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tom.utils.JsonUtil;
import com.tom.utils.ParameterUtil;
import com.tom.utils.TestContext;
import com.tom.utils.dto.DubboIdentityDto;

@Test(groups = "tool", enabled = false, alwaysRun = false)
public class JsonTest {
	ObjectMapper objectM = new ObjectMapper();

	@BeforeClass
	public void set() {
		objectM.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 生成json String
	 */
	@Test
	public void testw() {
		// AppBaseInfoVo infoVo = new AppBaseInfoVo();
		// infoVo.setAppId("asss");
		// infoVo.setAppName("tom_test2");
		// infoVo.setBusinessNo("112");
		// infoVo.setBusinessName("康辉旅游2");
		// infoVo.setBusinessType(1);
		// infoVo.setAppStatus(1);
		// infoVo.setUpdateUser("lsj2");
		// infoVo.set
		// infoVo.setBusinessNo("test_update_third");
		// infoVo.setUpdateUser("luosj");
//		 try {
		 DubboIdentityDto dto=new DubboIdentityDto();
		 dto.setInterfaceName("sdfsdfsdf");
		 System.out.println(new JsonUtil().objectToString(dto));
//		 } catch (IOException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
	}

	/**
	 * 从 string到实体类
	 */
	@Test
	public void testR() {
		// AppBaseInfoVo infoVo = null;
		try {
			Object infoVo = objectM.readValue(new File("d:/test.json"), Object.class);
			System.out.println(infoVo);
			new ParameterUtil().lastResultJsonString("profitCalculateType", "java.lang.String");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testR2() {
		// AppBaseInfoVo infoVo = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("d:/test.json"));
			String data = br.readLine();//一次读入一行，直到读入null为文件结束
			StringBuilder sBuilder=new StringBuilder();
			while( data!=null){
			      System.out.println(data);
			      sBuilder.append(data);
			      data = br.readLine(); //接着读下一行
			}
			String[] indexes=new String[]{"profitCalculateType"};
			String result="";
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(sBuilder.toString());
			JsonElement value = jsonElement;
			for (int i = 0; i < indexes.length; i++) {
				String index = indexes[i];
					value = value.getAsJsonObject().get(index);
			}
			if (value instanceof JsonObject || value instanceof JsonArray) {
				result+= value.getAsJsonObject().toString().trim();
			} else {
				result+= value.getAsString().trim();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * map 生成json String
	 */
	@Test
	public void mapT() {
		Map<String, String> ms = new HashMap<>();
		ms.put("appDirectUrl", "");
		ms.put("businessNo", "");
		try {
			// System.out.println(objectM.readValue(new File("d:/user.json"),
			// AppBaseInfoVo.class));
			System.out.println(objectM.writeValueAsString(ms));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 输入一个个map的key，生成json字符串
	 */
	@Test
	public void inputR() {
		StringBuilder sb = new StringBuilder("{");
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(System.in));
			String content = "";
			while (content != null) {
				content = bf.readLine().trim();
				if (content.isEmpty()) {
					break;
				}
				for (String string : content.split("\\s+")) {
					sb.append("\"" + string + "\"" + ":");
				}
				sb = sb.append("}");
				System.out.println(sb.toString());
				sb = new StringBuilder("{");

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
	}
}
