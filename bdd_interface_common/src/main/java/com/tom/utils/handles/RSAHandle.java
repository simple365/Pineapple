package com.tom.utils.handles;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.tom.utils.PropertiesUtil;

/**
 * RSA的类型，保存rsa的常量
 * 
 */
public class RSAHandle {
	
	private String rsaLocation=null;
	
	public void init(){
		String file=PropertiesUtil.getProperty("RSAKeyStore.file",FileHandle.PROPERTY_FILE);
		if(!StringUtils.isEmpty(file))
		rsaLocation=Thread.currentThread().getContextClassLoader().getResource(file).getFile();
	}

	public String getRsaLocation() {
		return rsaLocation;
	}

	public void setRsaLocation(String rsaLocation) {
		this.rsaLocation = rsaLocation;
	}
	
}
