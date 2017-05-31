package com.tom.utils.dto;

/**
 * 该类用于指定一个固定的dto
 * @author Administrator
 *
 */
public class DubboIdentityDto{
	
	public static final String INTERFACE_NAME="接口名";
	public static final String METHOD_NAME="方法名";
	public static final String PARAMETER_TYPES="参数类型";
	public static final String ARGS="参数";

	private String interfaceName;
	private String methodName;
	private String[] parameterTypes;
	private Object[] args;
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
}
