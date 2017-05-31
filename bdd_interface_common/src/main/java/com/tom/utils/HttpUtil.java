package com.tom.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tom.utils.factory.ClientFactory;

import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.com.google.gson.JsonParser;

public class HttpUtil {
	ParameterUtil parameterUtil = new ParameterUtil();
	Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	public final static String HEADERS = "_headers";

	CloseableHttpResponse response = null;

	// 创建一个本地上下文信息
	static HttpClientContext localContext = new HttpClientContext();

	public CloseableHttpResponse execute(HttpRequestBase request, CloseableHttpClient httpClient)
			throws ClientProtocolException, IOException {
		if (localContext.getCookieStore() != null && !localContext.getCookieStore().getCookies().isEmpty()) {
			final List<Header> headers = localContext.getCookieSpec().formatCookies(localContext.getCookieStore().getCookies());
			for (final Header header : headers) {
				request.setHeader(header.getName(), header.getValue());
			}
		}
		return httpClient.execute(request, localContext);
	}

	/**
	 * 将json封装成httpclient可用的参数
	 * 
	 * @param json
	 * @return
	 */
	public List<NameValuePair> packJson(String json) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(json);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> elEntry : jsonObject.entrySet()) {
			String value = null;
			if (!elEntry.getValue().isJsonNull()) {
				value = elEntry.getValue().getAsString();
			}
			NameValuePair nameValuePair = new BasicNameValuePair(elEntry.getKey(), value);
			result.add(nameValuePair);
		}
		return result;
	}

	/**
	 * post 请求，带头
	 * 
	 * @param url
	 * @param json
	 * @param headers
	 * @return
	 */
	public String requestPost(String url, String json, Map<String, String> headers) {
		// 处理字符串中的参数
		url = parameterUtil.parseParameter(url);
		json = parameterUtil.parseParameter(json);

		List<NameValuePair> params = packJson(json);
		String resultStr = null;
		CloseableHttpClient httpclient = ClientFactory.getHttpClient();
		HttpPost httppost = new HttpPost(url);

	    dealHeaders(httppost,headers);
		try {
			UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(params,"UTF-8");
			httppost.setEntity(httpEntity);
			response = this.execute(httppost, httpclient);
			logger.info("StatusCode -> " + response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			resultStr = EntityUtils.toString(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (resultStr == null) {
			logger.info("post返回 " + resultStr);
		} else {
			logger.info("post返回 " + resultStr.substring(0, 1000 > resultStr.length() ? resultStr.length() : 1000) + ".....");
		}
		// 保存结果
		TestContext.getInstance().setLastResult(resultStr);
		httppost.releaseConnection();
		return resultStr;
	}

	/**
	 * @param httppost
	 * @param headers2
	 */
	private void dealHeaders(HttpRequestBase request, Map<String, String> headers) {
		//添加uid-业务需求
		request.addHeader(LogTools.UID, LogTools.generateUID());
		// 添加默认的头参数,冲突的话以传入的为准
		Map<String, String> settedHeaders = (Map<String, String>) parameterUtil.getParameter(HttpUtil.HEADERS,new HashMap<>());
		if (headers != null)
			settedHeaders.putAll(headers);
		if (null != settedHeaders && !settedHeaders.isEmpty()) {
			for (Entry<String, String> etn : settedHeaders.entrySet()) {
				request.addHeader(etn.getKey(), etn.getValue());
			}
		}
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param json
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String requestPost(String url, String json) {
		// 处理字符串中的参数
		return requestPost(url, json, null);
	}

	public String requestGet(String urlWithParams, Map<String, String> headers) {
		// 处理字符串中的参数
		urlWithParams = parameterUtil.parseParameter(urlWithParams);
		String resultStr = null;
		CloseableHttpClient httpclient = ClientFactory.getHttpClient();
		HttpGet httpget = new HttpGet(urlWithParams);
	
		dealHeaders(httpget,headers);
		try {
			response = this.execute(httpget, httpclient);
			logger.info("StatusCode -> " + response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			resultStr = EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (resultStr == null) {
			logger.info("get返回 " + resultStr);
		} else {
			logger.info("get返回 " + resultStr.substring(0, 1000 > resultStr.length() ? resultStr.length() : 1000) + ".....");
		}
		// 保存结果
		TestContext.getInstance().setLastResult(resultStr);
		httpget.releaseConnection();
		return resultStr;
	}

	public String requestGet(String urlWithParams) {
		return requestGet(urlWithParams, null);
	}

	public void closeSession() {
		try {
			ClientFactory.getHttpClient().close();
			ClientFactory.dismiss();
			if (localContext.getCookieStore() != null)
				localContext.getCookieStore().clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 从一个get链接中拿到相应的值
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getUrlValue(String key, String url) throws UnsupportedEncodingException {
		return getParamsMap(url).get(key);
	}

	public Map<String, String> getParamsMap(String url) throws UnsupportedEncodingException {
		Map<String, String> paramsMap = new HashMap<String, String>();
		int index = url.indexOf("?");
		String urlParams = "";
		if (index != -1) {
			urlParams = url.substring(index + 1);
			if (urlParams == null || "".equals(urlParams.trim()) || "null".equalsIgnoreCase(urlParams.trim())) {
				return paramsMap;
			}
			String[] paramsArray = urlParams.split("&");
			for (String params : paramsArray) {
				String[] paramPair = params.split("=");
				String param = paramPair[0];
				String value = paramPair.length == 1 ? "" : paramPair[1];
				if (!"".equals(value)) {
					value = URLDecoder.decode(value, "UTF-8");
				}
				paramsMap.put(param, value);
			}
		}
		return paramsMap;
	}

	/**
	 * 获取当前链接的url
	 * 
	 * @return
	 */
	public String getCurrentUrl() {
		HttpHost currentHost = (HttpHost) localContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
		HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
		return (req.getURI().isAbsolute()) ? req.getURI().toString() : (currentHost.toURI() + req.getURI());
	}

	public List<String> getHeadValues(List<String> heads) {
		List<String> values = new ArrayList<>();
		for (String head : heads) {
			values.add(response.getFirstHeader(head).getValue());
		}
		return values;
	}
}
