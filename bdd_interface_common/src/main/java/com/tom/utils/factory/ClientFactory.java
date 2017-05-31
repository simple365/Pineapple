package com.tom.utils.factory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.ssl.SSLContexts;

public class ClientFactory {

	static HttpClient httpClient;

	public static HttpClient create() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					return true;
				}
			}).build();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		RequestConfig globalConfig = RequestConfig.custom().setRedirectsEnabled(true)
//	            .setCookieSpec(CookieSpecs.DEFAULT)
	            .build();
		HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setSSLSocketFactory(sslsf).setRedirectStrategy(new LaxRedirectStrategy()).build();
		// httpclient=HttpClients.createDefault();
		return httpclient;
	}

	/**
	 * 使用这个方法获得 httpClient
	 * 
	 * @return
	 */
	public static CloseableHttpClient getHttpClient() {
		if (httpClient == null)
			httpClient = create();
		return (CloseableHttpClient) httpClient;
	}

	public static void close() {
		if (httpClient != null) {
			try {
				((CloseableHttpClient) httpClient).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	public static void dismiss() {
		httpClient = null;
	}
}
