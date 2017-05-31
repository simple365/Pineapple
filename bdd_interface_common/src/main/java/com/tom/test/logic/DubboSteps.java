package com.tom.test.logic;

import java.util.ArrayList;
import java.util.List;

import com.tom.utils.ParameterUtil;
import com.tom.utils.TestContext;
import com.tom.utils.dto.DubboIdentityDto;
import com.tom.utils.dto.TypeValueEntry;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import gherkin.formatter.model.DocString;

public class DubboSteps {
	ParameterUtil paraUtil = new ParameterUtil();

	@And("^Dubbo调用:(.+?),参数列表:$")
	public void dubboInvokeTable(String path, List<TypeValueEntry> entris) {
		String interfaceName = path.substring(0, path.lastIndexOf("."));
		String methodName = path.substring(path.lastIndexOf(".") + 1);
		DubboIdentityDto dubboIdentityDto = new DubboIdentityDto();
		dubboIdentityDto.setInterfaceName(interfaceName);
		dubboIdentityDto.setMethodName(methodName);
		if (entris != null && !entris.isEmpty()) {
			List<String> types = new ArrayList<>();
			List<Object> args = new ArrayList<>();
			for (TypeValueEntry typeValueEntry : entris) {
				String key = paraUtil.parseParameter(typeValueEntry.类型.trim());
				types.add(key);
				String value = paraUtil.parseParameter(typeValueEntry.值.trim());
				Object vas = null;
				vas = TestContext.getInstance().getDubboHandle().parseValue(key, value);
				args.add(vas);
			}
			dubboIdentityDto.setParameterTypes(types.toArray(new String[types.size()]));
			dubboIdentityDto.setArgs(args.toArray());
		}
		TestContext.getInstance().getDubboHandle().dubboExecute(dubboIdentityDto);
	}

	@And("^Dubbo调用:(.+?),参数:(.*)$")
	public void dubboInvokeTable(String path, String paras) {
		path = paraUtil.parseParameter(path);
		paras = paraUtil.parseParameter(paras);

		String interfaceName = path.substring(0, path.lastIndexOf("."));
		String methodName = path.substring(path.lastIndexOf(".") + 1);
		DubboIdentityDto dubboIdentityDto = TestContext.getInstance().getDubboHandle().parseDubboParams(paras.trim());
		dubboIdentityDto.setInterfaceName(interfaceName);
		dubboIdentityDto.setMethodName(methodName);
		TestContext.getInstance().getDubboHandle().dubboExecute(dubboIdentityDto);
	}

	@And("^Dubbo调用:(.+?),参数内容:$")
	public void dubboInvokeDoc(String path, String paras) {
		path = paraUtil.parseParameter(path);
		paras = paraUtil.parseParameter(paras);

		String interfaceName = path.substring(0, path.lastIndexOf("."));
		String methodName = path.substring(path.lastIndexOf(".") + 1);
		DubboIdentityDto dubboIdentityDto = TestContext.getInstance().getDubboHandle().parseDubboParams(paras.trim(),"\n");
		dubboIdentityDto.setInterfaceName(interfaceName);
		dubboIdentityDto.setMethodName(methodName);
		TestContext.getInstance().getDubboHandle().dubboExecute(dubboIdentityDto);
	}

	// 验证部分****************************
	@Then("^验证dubbo异常中存在:(.*)$")
	public void dExceptionContains(List<String> content) {
		if (content == null || content.isEmpty()) {
			return;
		}
		String lastResult = TestContext.getInstance().getExceptionHandle().getDubboException();
		for (int i = 0; i < content.size(); i++) {
			String conts = paraUtil.parseParameter(content.get(i));
			if (!lastResult.contains(conts)) {
				throw new RuntimeException(conts + " 无法在返回内容中找到。");
			}
		}

	}
}
