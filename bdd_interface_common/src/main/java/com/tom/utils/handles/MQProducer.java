package com.tom.utils.handles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.netease.cloud.nqs.client.ClientConfig;
import com.netease.cloud.nqs.client.Message;
import com.netease.cloud.nqs.client.exception.MessageClientException;
import com.netease.cloud.nqs.client.producer.ProducerConfig;
import com.netease.cloud.nqs.client.push.PushMessageClient;
import com.rabbitmq.client.ShutdownSignalException;
import com.tom.config.MQConfig;
import com.tom.utils.PropertiesUtil;

/**
 * @author Luo Shengjie mq 写入操作
 */
public class MQProducer {

	private MQConfig mqConfig;

	private volatile PushMessageClient pushMessageClient;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final static int DEFAULT_RETRY_TIMES = 3;

	private int retryTimes = DEFAULT_RETRY_TIMES;

	private final static long RETRY_INTERVAL = 500;

	/**
	 * 读取配置文件
	 */
	public void init() {
		if (pushMessageClient != null) {
			return;
		}
		// 初始化mqConfig
		if (mqConfig == null) {
			String fileLocation = Thread.currentThread().getContextClassLoader().getResource("mq.properties").getPath();
			mqConfig = new MQConfig();
			mqConfig.setHost(PropertiesUtil.getPropertyByEnv("mq.host", fileLocation));
			mqConfig.setConfirmTimeout(
					Long.parseLong(PropertiesUtil.getPropertyByEnv("mq.producer.confirmTimeout", fileLocation)));
			mqConfig.setExchange(PropertiesUtil.getPropertyByEnv("mq.exchange", fileLocation));
			mqConfig.setHost(PropertiesUtil.getPropertyByEnv("mq.host", fileLocation));
			mqConfig.setPassword(PropertiesUtil.getPropertyByEnv("mq.password", fileLocation));
			mqConfig.setPort(Integer.parseInt(PropertiesUtil.getPropertyByEnv("mq.port", fileLocation)));
			mqConfig.setPrefetchCount(Integer.parseInt(PropertiesUtil.getPropertyByEnv("mq.consumer.prefetchCount", fileLocation)));
			mqConfig.setRequireConfirm(Boolean.parseBoolean(PropertiesUtil.getPropertyByEnv("mq.producer.confirm", fileLocation)));
			mqConfig.setUsername(PropertiesUtil.getPropertyByEnv("mq.username", fileLocation));
		}
		ClientConfig cc = mqConfig.getClientConfig();

		ProducerConfig pc = new ProducerConfig();
		pc.setProductId(mqConfig.getExchange());
		pc.setQueueName("useless");
		pc.setRequireConfirm(mqConfig.isRequireConfirm());
		pc.setWaitTimeout(mqConfig.getConfirmTimeout());

		try {
			pushMessageClient = new PushMessageClient(cc, pc);
		} catch (MessageClientException e) {
			throw new RuntimeException("init mq producer fail", e);
		}
		// logger.info(Log.op(LogOp.MQ_PRODUCER_INIT).msg("init suc").toString());
	}

	/**
	 * 发送消息得某个队列
	 * 
	 * @param message
	 *            要发送的消息
	 * @param routingKey
	 *            发送的队列(路由键)
	 */
	public void sendMessage(String message, String routingKey) {
		int retry = 0;
		boolean connShutdowned = false;
		boolean sendSuc = false;
		while (!sendSuc && retry++ <= retryTimes) {
			try {
				if (connShutdowned) {
					init();
				}
				Message mess = new Message(message.getBytes("UTF-8"), true);
				pushMessageClient.sendMessageWithRoutingKey(mess, routingKey);
				sendSuc = true;
			} catch (ShutdownSignalException ex) {
				// logger.warn(Log.op(LogOp.MQ_PRODUCER_FAIL).msg("shutdownSignal").kv("errorMsg",
				// ex.getMessage()).toString());
				ex.printStackTrace();
				destory();
				retry++;
				connShutdowned = true;
				try {
					Thread.sleep(RETRY_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception ex) {
//				 logger.warn(Log.op(LogOp.MQ_PRODUCER_FAIL).msg("exception occur").kv("errorMsg", ex.getMessage()).toString(), ex);
				ex.printStackTrace();
				destory();
				retry++;
				connShutdowned = true;
				try {
					Thread.sleep(RETRY_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (!sendSuc) {
			throw new RuntimeException("send mq message fail");
		}
	}

	/**
	 * 发送消息得某个队列
	 * 
	 * @param message
	 *            要发送的消息
	 * @param routingKey
	 *            发送的队列(路由键)
	 */
	public void sendMessageWithTraceId(String message, String routingKey) {
		/**
		 * if (trace==null||!trace.isOn()) { sendMessage(message,routingKey); }
		 */
		String spanName = mqConfig.getExchange() + "." + routingKey;
		sendWithTrace(message, routingKey, spanName);
	}

	public void sendMessageWithTraceId(String identify, String message, String routingKey) {

		String spanName = mqConfig.getExchange() + "." + routingKey + "." + identify;
		sendWithTrace(message, routingKey, spanName);
	}

	private void sendWithTrace(String message, String routingKey, String spanName) {
		RpcContext context = RpcContext.getContext();
		boolean consumerSide = true;
		// Span span = null;// 本次调用的span
		// Endpoint endpoint = new Endpoint(TraceWebUtils.getIPAddress(), TraceWebUtils.getHostName(),
		// context.getLocalPort());
		//
		// span = buildSpan(consumerSide, spanName, null);
		// span.setHost(endpoint);
		// span.setAppName(trace.getAppName());
		//
		// startInvoke(span, consumerSide);
		// // 要把span放置到消息中
		// Preconditions.checkNotNull(span, "span is null:" + span);
		// MQTransInfo info = new MQTransInfo();
		// info.setMessage(message);
		// getConcurrent(spanName).incrementAndGet(); // 并发计数
		// Map<String, String> attachments = new HashMap<String, String>();
		// // 调用其他Dubbo服务必须设置是否采样
		// attachments.put(TraceClientConst.IS_SAMPLE, String.valueOf(span.isSample()));
		// attachments.put(TraceClientConst.SPAN_ID, String.valueOf(span.getId()));
		// attachments.put(TraceClientConst.PARENT_ID, String.valueOf(span.getParentId()));
		// attachments.put(TraceClientConst.RPC_ID, String.valueOf(span.getRpcId()));
		// attachments.put(TraceClientConst.TRACE_ID, String.valueOf(span.getTraceId()));
		// attachments.put(MQTraceUtil.spanName, String.valueOf(spanName));
		// info.setAttachments(attachments);
		// try {
		// this.sendMessage(JSON.toJSONString(info), routingKey);
		// } finally {
		// endInvoke(span, consumerSide);
		// // 记录并发数据
		// int concurrent = getConcurrent(spanName).get(); // 当前并发数
		// trace.logConcurrent(span, concurrent);
		// // Log span
		// trace.logSpan(span);
		// getConcurrent(spanName).decrementAndGet(); // 并发计数
		// }
	}

	/**
	 * 销毁生产者
	 */
	public synchronized void destory() {
		if (pushMessageClient != null) {
			// logger.info(Log.op(LogOp.MQ_PRODUCER_SHUTDOWN).msg("destory start").toString());
			try {
				pushMessageClient.shutdown();
			} catch (ShutdownSignalException ex) {
				// logger.warn(Log.op(LogOp.MQ_DESTORY_FAIL).kv("msg", ex.getMessage()).toString());
			}
			pushMessageClient = null;
			// logger.info(Log.op(LogOp.MQ_PRODUCER_SHUTDOWN).msg("destory done").toString());
		} else {
			logger.info("nothing to destory");
		}
	}
}
