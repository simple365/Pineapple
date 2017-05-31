package com.tom.config;

import com.netease.cloud.nqs.client.ClientConfig;

/**
 * mq相关的配置
 * 
 * @author zhengxiaohong
 */
public class MQConfig {

	private String host;

	private int port;

	private String username;

	private String password;

	private String exchange;

	/** 生产者相关 消息是否需要confirm */
	private boolean requireConfirm;

	/** 消息confirm的超时时间(单位:秒) */
	private long confirmTimeout;

	/**
	 * 消费者相关
	 */
	private int prefetchCount;

	/**
	 * 生成nqs相关的配置类
	 * 
	 * @return
	 */
	 public ClientConfig getClientConfig() {
	 ClientConfig cc = new ClientConfig();
	 cc.setHost(host);
	 cc.setPort(port);
	 cc.setAccessKey(username);
	 cc.setAccessSecret(password);
	 cc.setAuthMechanism(ClientConfig.AUTH_PLAIN);
	 cc.setProductId(exchange);
	 return cc;
	 }

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the exchange
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param exchange
	 *            the exchange to set
	 */
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}


	public boolean isRequireConfirm() {
		return requireConfirm;
	}

	public void setRequireConfirm(boolean requireConfirm) {
		this.requireConfirm = requireConfirm;
	}

	public long getConfirmTimeout() {
		return confirmTimeout;
	}

	public void setConfirmTimeout(long confirmTimeout) {
		this.confirmTimeout = confirmTimeout;
	}

	public int getPrefetchCount() {
		return prefetchCount;
	}

	public void setPrefetchCount(int prefetchCount) {
		this.prefetchCount = prefetchCount;
	}
}
