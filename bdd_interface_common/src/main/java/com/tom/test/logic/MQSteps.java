package com.tom.test.logic;

import com.tom.utils.TestContext;
import com.tom.utils.handles.MQProducer;

import cucumber.api.java.en.And;

/**
 * @author Luo Shengjie
 *
 */
public class MQSteps {

	@And("^MQ写入:(.+?)队列:(.+)$")
	public void mqWrite(String queue ,String message){
		MQProducer mqProducer=TestContext.getInstance().getMqProducer();
		mqProducer.init();
		mqProducer.sendMessage(message, queue);
	}
}
