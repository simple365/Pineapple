package com.tom.test.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.utils.HttpUtil;
import com.tom.utils.ParameterUtil;
import com.tom.utils.TestContext;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.com.google.gson.JsonParser;

public class WebSteps {
	HttpUtil httputl = new HttpUtil();
	ParameterUtil paraUtil = new ParameterUtil();
	Logger logger=LoggerFactory.getLogger(WebSteps.class);
	
	@And("^将Http响应头中的:(.+)保存为参数(.+)$")
	public void getHeaders(List<String> headers,List<String> paras){
		List<String> values=httputl.getHeadValues(headers);
		for (int i = 0; i < paras.size(); i++) {
			paraUtil.saveScenarioParaString(paras.get(i), values.get(i));
		}
	}

	@And("^获得url:(.+)中的:(.+),并保存为参数(.+)$")
	public void operateUrl(String url, List<String> names, List<String> paras) throws UnsupportedEncodingException {
		url = paraUtil.parseParameter(url);
		HttpUtil httpUtil = new HttpUtil();
		for (int i = 0; i < names.size(); i++) {
			String value = httpUtil.getUrlValue(names.get(i), url);
			paraUtil.saveScenarioParaString(paras.get(i), value);
		}
	}

	@And("^get访问链接:(.+),jsoup将元素:(.+)的value保存为(.+)$")
	public void jsoupSetPara(String url, List<String> selectors, List<String> paras) throws IOException {
		url = paraUtil.parseParameter(url);
		Document doc = Jsoup.connect(url).get();
		for (int i = 0; i < selectors.size(); i++) {
			String select = paraUtil.parseParameter(selectors.get(i));
			paraUtil.saveScenarioParaString(paras.get(i).trim(), doc.select(select).attr("value"));
		}
	}

	@And("^获得当前url中的:(.+),并保存为参数(.+)$")
	public void urlPara(List<String> names, List<String> paras) throws IOException {
		String url = new HttpUtil().getCurrentUrl();
		HttpUtil httpUtil = new HttpUtil();
		for (int i = 0; i < names.size(); i++) {
			String value = httpUtil.getUrlValue(names.get(i), url);
			paraUtil.saveScenarioParaString(paras.get(i), value);
		}
	}
	
	@Given("^post请求接口:(.+?),(.+)$")
	public void testprar(String url, String json) {
		url = paraUtil.parseParameter(url);
		json = paraUtil.parseParameter(json);
		logger.info("post 请求：" + url + "，参数:" + json);
		httputl.requestPost(url, json);
	}
	
	private Map<String,String> handleHead(String headStr){
		String[] vals=headStr.split(",");
		Map<String,String> headers=new HashMap<String,String>();
		for(int i=0;i<vals.length;i++){
			headers.put(vals[i].split(":")[0],vals[i].split(":")[1]);
		}
		return headers;
	}
	
	@Given("^post头:(.+?)请求接口:(.+?),(.+)$")
	public void testprarHead(String headStr,String url, String json) {
		url = paraUtil.parseParameter(url);
		json = paraUtil.parseParameter(json);
		headStr = paraUtil.parseParameter(headStr);
		logger.info("post 请求：" + url + "，参数:" + json+" ，头"+headStr);
		httputl.requestPost(url, json,handleHead(headStr));
	}

	@And("^get头:(.+?)请求链接:(.+)$")
	public void getUrl(String headStr,String urlWithParams) {
		urlWithParams = paraUtil.parseParameter(urlWithParams);
		headStr = paraUtil.parseParameter(headStr);
		logger.info("get 请求：" + urlWithParams+"，头:"+headStr);
		httputl.requestGet(urlWithParams,handleHead(headStr));
	}
	@And("^get请求链接:(.+)$")
	public void getUrl(String urlWithParams) {
		urlWithParams = paraUtil.parseParameter(urlWithParams);
		logger.info("get 请求：" + urlWithParams);
		httputl.requestGet(urlWithParams);
	}

	@And("^get参数请求:(.+?),(.+)$")
	public void getUrlByJson(String urlWithParams, String json) {
		urlWithParams = paraUtil.parseParameter(urlWithParams);
		json = paraUtil.parseParameter(json);
		StringBuffer sb=new StringBuffer();
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(json);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> elEntry : jsonObject.entrySet()) {
			String value=null;
			if(!elEntry.getValue().isJsonNull()){
				value=elEntry.getValue().getAsString();
			}
			sb.append("&").append(elEntry.getKey()+"="+value);
		}
		httputl.requestGet(urlWithParams+"?"+URLEncoder.encode(sb.substring(1)));
	}
	
	@And("^添加场景http头:(.+)$")
	public void httpAddHeader(String headers){
		Map<String, String> paras=packHeaders(headers);
		TestContext.getInstance().putScenarioParameter(HttpUtil.HEADERS,paras);
	}
	
	@And("^添加用例集http头:(.+)$")
	public void httpAddHeaderFeaturely(String headers){
		Map<String, String> paras=packHeaders(headers);
		TestContext.getInstance().putFeatureParameter(HttpUtil.HEADERS,paras);
	}
	
	@And("^添加全局http头:(.+)$")
	public void httpAddHeaderGlobaly(String headers){
		Map<String, String> paras=packHeaders(headers);
		TestContext.getInstance().putGlobalParameter(HttpUtil.HEADERS,paras);
	}
	
	private Map<String,String> packHeaders(String headers){
		headers=paraUtil.parseParameter(headers);
		String[] heads=headers.split(",");
		Map<String, String> paras=new HashMap<>();
		for(String head:heads){
			int index=head.indexOf(":");
			paras.put(head.substring(0,index), head.substring(index+1));
		}
		return paras;
	}
}
