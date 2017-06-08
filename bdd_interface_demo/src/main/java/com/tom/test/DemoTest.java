package com.tom.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

import com.tom.utils.HttpUtil;
import com.tom.utils.ParameterUtil;
import com.tom.utils.factory.ClientFactory;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;

public class DemoTest {
	int a;
	int b;
	int result;

	@Given("输入值 (.+)")
	public void test1(List<Integer> ints) {
		a = ints.get(0);
		b = ints.get(1);
	}

	@And("计算两个值相乘")
	public void multiply() {
		result = a * b;
	}

	@Then("验证结果等于 (\\d+)")
	public void resutlVerify(int expected) {
		Assert.assertEquals(expected, result);
	}

	@When("^I see the following cooked I should say:$")
	public void theFoodResponse(List<Entry> entries) {
		for (Entry entry : entries) {
			// Test actual app you've written
			System.out.println(entry.food + "---" + entry.say);

		}
	}

	public class Entry {
		String food;
		String say;
	}

	@Then("生成随机数:(\\d+)")
	public void erify(int expected) {
		// Assert.assertEquals(expected, result);
		for (int i = 0; i < expected; i++) {
			System.out.println(new Random().nextInt(expected));
		}
	}

	@Given("^oms登录:(.+),(.+),(.+)")
	public void login(String url, String username, String userpwd) throws ClientProtocolException, IOException {
		url = new ParameterUtil().parseParameter(url);
		CloseableHttpResponse response = null;
		CookieStore cookieStorea = new BasicCookieStore();
		CloseableHttpClient httpclient = ClientFactory.getHttpClient();
		HttpClientContext context = HttpClientContext.create();
		HttpGet httpget = new HttpGet(url + "/openid_connect_login");
		response = httpclient.execute(httpget, context);
		HttpHost target = context.getTargetHost();
		System.out.println("url " +target.toURI());
		HttpPost httppost = new HttpPost(target.toURI() + "/j_spring_security_check");
		LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
		// httpclient =
		// HttpClients.custom().setRedirectStrategy(redirectStrategy).setDefaultCookieStore(cookieStorea).build();
		// httpclient = HttpClients.custom().setRedirectStrategy(redirectStrategy).build();
		List<NameValuePair> dataList = new ArrayList<NameValuePair>();
		dataList.add(new BasicNameValuePair("j_username", username));
		dataList.add(new BasicNameValuePair("j_password", userpwd));
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(dataList, Charsets.UTF_8);
		httppost.setEntity(uefEntity);
		new HttpUtil().requestPost("http://10.166.224.235:8182" + "/j_spring_security_check",
				"{\"j_username\":\"qudefeng@myhome.163.com\",\"j_password\":\"test123\"}");
		// response = httpclient.execute(httppost);
//		System.out.println("返回结果是:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
	}
}
