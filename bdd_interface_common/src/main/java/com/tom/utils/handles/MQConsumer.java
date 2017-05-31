package com.tom.utils.handles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.cloud.nqs.client.ClientConfig;
import com.netease.cloud.nqs.client.SimpleMessageSessionFactory;
import com.netease.cloud.nqs.client.consumer.ConsumerConfig;
import com.netease.cloud.nqs.client.consumer.MessageConsumer;
import com.netease.cloud.nqs.client.consumer.MessageHandler;
import com.netease.cloud.nqs.client.exception.MessageClientException;
import com.rabbitmq.client.ShutdownSignalException;
import com.tom.config.MQConfig;

/**
 * RabbitMQ消费者的封装
 * 
 * @author zhengxiaohong
 */
public class MQConsumer {

	private MQConfig mqConfig;

	private SimpleMessageSessionFactory sessionFactory;

	private Map<String, MessageConsumer> consumerMap;

	private Map<String, MQConsumerRunner> consumerRunnerMap;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final static long RE_CONN_INTERVAL = 3000;

	/**
	 * 新建消费者
	 * 
	 * @param mqConfig
	 *            RabbitMQ配置
	 * @param mqConsumerConfig
	 *            消费者配置
	 */
	public MQConsumer(MQConfig mqConfig) {
		this.mqConfig = mqConfig;
	}

	/**
	 * 初始化
	 */
	public void init() {
		if (mqConfig == null) {
			throw new RuntimeException("mqConfig can not be null");
		}
		consumerMap = new ConcurrentHashMap<String, MessageConsumer>();
		consumerRunnerMap = new ConcurrentHashMap<String, MQConsumerRunner>();
		init(mqConfig);
	}

	/**
	 * @param mqConfig
	 * @param mqConsumerConfig
	 */
	private synchronized void init(MQConfig mqConfig) {
		ClientConfig cc = mqConfig.getClientConfig();
		sessionFactory = new SimpleMessageSessionFactory(cc);
	}

	private synchronized void initConsumer(String queueName) {
		if (consumerMap.get(queueName) != null) {
			return;
		}
		ConsumerConfig config = new ConsumerConfig();
		config.setGroup("test");
		config.setProductId(mqConfig.getExchange());
		config.setPrefetchCount(mqConfig.getPrefetchCount());
		config.setRequireAck(true);
		config.setQueueName(queueName);

		try {
			MessageConsumer messageConsumer = sessionFactory.createConsumer(config);
			consumerMap.put(queueName, messageConsumer);
			// logger.info(Log.op(LogOp.MQ_CONSUMER_INIT).kv("queue", queueName).toString());
		} catch (MessageClientException e) {
			sessionFactory.shutdown();
		}
	}

	/**
	 * 启动1个线程来消费某个队列的消息
	 * 
	 * @param queueName
	 *            队列名称
	 * @param handler
	 *            处理消息的handler
	 */
	public void consumeMessage(String queueName, MessageHandler handler) {
		initConsumer(queueName);
		// logger.info(Log.op(LogOp.MQ_CONSUME_MSG).kv("queue", queueName).toString());
		MessageConsumer messageConsumer = consumerMap.get(queueName);
		if (messageConsumer == null) {
			throw new RuntimeException("init initConsumer fail, queueName:" + queueName);
		}
		startMQConsumerThread(queueName, handler, messageConsumer);
	}

	/**
	 * 启动1个线程来消费某个队列的消息
	 * 
	 * @param queueName
	 *            队列名称
	 * @param handler
	 *            处理消息的handler
	 */
	public void consumeMessageWithTraceId(String queueName, MessageHandler handler) {
		initConsumer(queueName);
		// logger.info(Log.op(LogOp.MQ_CONSUME_MSG).kv("queue", queueName).toString());
		MessageConsumer messageConsumer = consumerMap.get(queueName);
		if (messageConsumer == null) {
			throw new RuntimeException("init initConsumer fail, queueName:" + queueName);
		}
		startMQConsumerThread(queueName, handler, messageConsumer);
	}

	/**
	 * 启动mq消费者线程
	 * 
	 * @param queueName
	 * @param handler
	 * @param messageConsumer
	 */
	private void startMQConsumerThread(String queueName, MessageHandler handler, MessageConsumer messageConsumer) {
		MQConsumerRunner runner = new MQConsumerRunner(queueName, messageConsumer, handler);
		consumerRunnerMap.put(queueName, runner);
		Thread consumerThread = new Thread(runner, "framework-mq-consumer-" + queueName);
		consumerThread.start();
	}

	/**
	 * 销毁消费者
	 */
	public synchronized void destory() {
		// logger.info(Log.op(LogOp.MQ_CONSUME_SHUTDOWN).msg("MQConsumer Factory destory start").toString());
		if (sessionFactory != null) {
			sessionFactory.shutdown();
		}
		if (consumerMap == null || consumerMap.isEmpty()) {
			return;
		}
		for (String queueName : consumerMap.keySet()) {
			MQConsumerRunner runner = consumerRunnerMap.get(queueName);
			if (runner != null) {
				runner.shutdown();
			}
			MessageConsumer messageConsumer = consumerMap.get(queueName);
			if (messageConsumer != null) {
				messageConsumer.shutdown();
			}
		}
		consumerRunnerMap.clear();
		consumerMap.clear();
		// logger.info(Log.op(LogOp.MQ_CONSUME_SHUTDOWN).msg("MQConsumer Factory destory done").toString());
	}

	/**
	 * 消费者线程
	 * 
	 * @author zhengxiaohong
	 *
	 */
	class MQConsumerRunner implements Runnable {
		String queueName;
		MessageConsumer messageConsumer;
		MessageHandler handler;
		private volatile boolean running = true;

		public MQConsumerRunner(String queueName, MessageConsumer messageConsumer, MessageHandler handler) {
			this.queueName = queueName;
			this.messageConsumer = messageConsumer;
			this.handler = handler;
		}

		@Override
		public void run() {
			while (running) {
				try {
					messageConsumer.consumeMessage(handler);
				} catch (ShutdownSignalException ex) {
					// logger.warn(Log.op(LogOp.MQ_CONSUME_FAIL).toString(), ex);
					messageConsumer = reConnect(messageConsumer, queueName, handler);
				} catch (MessageClientException ex) {
					// logger.warn(Log.op(LogOp.MQ_CONSUME_FAIL).toString(), ex);
					messageConsumer = reConnect(messageConsumer, queueName, handler);
				}
			}
			// 关闭最后的连接, 清空队列
			// logger.info(Log.op(LogOp.MQ_CONSUME_HOOK).msg("thread exit").toString());
		}

		private MessageConsumer reConnect(MessageConsumer oldConsumer, String queueName, MessageHandler handler) {
			// 如果进程要退出的话, 就不需要重建连接了
			if (!running) {
				return oldConsumer;
			}
			consumerMap.remove(queueName);
			// 关闭旧连接
			oldConsumer.shutdown();
			// TODO 重连次数越多的话,重连间隔应该增长
			while (!consumerMap.containsKey(queueName)) {
				// logger.warn(Log.op(LogOp.MQ_CONSUME_RE_CONN).msg("try to reConnect").kv("queue",
				// queueName).toString());
				initConsumer(queueName);
				if (consumerMap.containsKey(queueName)) {
					break;
				} else {
					try {
						Thread.sleep(RE_CONN_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// logger.warn(Log.op(LogOp.MQ_CONSUME_RE_CONN).msg("reConnect suc").kv("queue", queueName).toString());
			return consumerMap.get(queueName);
		}

		private void shutdown() {
			this.running = false;
		}
	}

}