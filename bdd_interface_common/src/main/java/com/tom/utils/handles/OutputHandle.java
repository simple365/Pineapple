package com.tom.utils.handles;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tom.utils.PropertiesUtil;
import com.tom.utils.TestConstants;

/**
 * @author Luo Shengjie 将参数保存为properties文件
 */
public class OutputHandle {
	Logger logger = LoggerFactory.getLogger(OutputHandle.class);
	String path = null;

	public OutputHandle() {
		String tpath = System.getProperty(TestConstants.OUT_PUT_PROPERTIES);
		if (tpath == null)
			tpath = PropertiesUtil.getProperty(TestConstants.OUT_PUT_PROPERTIES,
					Thread.currentThread().getContextClassLoader().getResource(TestConstants.TEST_FILE).getFile());
		try {
			path = new URL("file:" + tpath).getFile();
			File file = new File(path);
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			logger.error("test.properties配置中，该路径文件无法创建:"+tpath);
		}
	}

	/**
	 * 追加参数到 文件中
	 * 
	 * @param key
	 * @param value
	 */
	public void writePara(String key, Object value) {
		String val = null;
		try {
			if (value instanceof String) {
				val = value.toString();
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				val = objectMapper.writeValueAsString(val);
			}
			PropertiesUtil.setProperty(key, val, path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
