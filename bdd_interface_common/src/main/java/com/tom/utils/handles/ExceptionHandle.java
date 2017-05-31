package com.tom.utils.handles;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.tom.utils.ParameterUtil;
import com.tom.utils.TestContext;

public class ExceptionHandle {
	
	public String setDubboException(Throwable e) {
		StringWriter stringWriter=new StringWriter();
		PrintWriter printWriter=new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String msg = stringWriter.toString();
		TestContext.getInstance().putScenarioParameter("dubboException", msg);
		return msg;
	}
	
	public String getDubboException(){
		return new ParameterUtil().getParaInString("dubboException");
	}
}
