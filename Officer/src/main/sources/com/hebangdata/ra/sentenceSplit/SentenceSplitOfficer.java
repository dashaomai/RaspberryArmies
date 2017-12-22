package com.hebangdata.ra.sentenceSplit;

import com.hebangdata.ra.consts.Commucation;
import com.hebangdata.ra.utils.SerializableUtils;
import com.hebangdata.ra.vos.SentenceSplitParameter;
import com.hebangdata.ra.vos.SentenceSplitResponse;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SentenceSplitOfficer {
	private static final Logger log = LoggerFactory.getLogger("SentenceSplitOfficer");

	public void Run(final String host, final SentenceSplitParameter parameter) {
		final ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);

		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();

			// 创建交换机
			channel.exchangeDeclare(parameter.getExchangerName(), BuiltinExchangeType.TOPIC, true);

			// 创建 ID 和接受返回值的队列
			final String correlationId = UUID.randomUUID().toString();
			final String replyQueue = channel.queueDeclare().getQueue();
			final AMQP.BasicProperties properties = new AMQP.BasicProperties()
					.builder()
					.correlationId(correlationId)
					.replyTo(replyQueue)
					.build();

			// 把任务拆分成多个子任务，每 10000 行拆成一个子任务
			final int subSize = 10000;
			final int subBatch = (int)Math.floor(parameter.lines.length / subSize) + 1;
			final BlockingQueue<SentenceSplitResponse> responses = new ArrayBlockingQueue<>(subBatch);

			channel.basicConsume(replyQueue, true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					if (properties.getCorrelationId().equals(correlationId)) {
						final SentenceSplitResponse response = SerializableUtils.getObject(body);

						if (null != response) responses.offer(response);
					} else {
						log.warn("回调回来的 correlationId 不符：{} - {}", properties.getCorrelationId(), correlationId);
					}
				}
			});

			for (int i = 0; i < subBatch; i++) {
				final SentenceSplitParameter subParameter = new SentenceSplitParameter(parameter.filePath, parameter.getSubLines(i * subSize, subSize));
				channel.basicPublish(parameter.getExchangerName(), Commucation.SENTENCE_SPLIT_ROUTING_KEY_A, true, properties, SerializableUtils.getBytes(subParameter));

				log.info("发送了一次子任务：{} - {}", i * subSize, subSize);
			}

			for (int i = 0; i < subBatch; i++) {
				final SentenceSplitResponse response = responses.poll(60, TimeUnit.SECONDS);

				if (null == response) break;

				log.info("获得反馈：{}", response);
			}
		} catch (TimeoutException | IOException | InterruptedException e) {
			log.error("消息分布期间出错：{}", e);
		} finally {
			if (null != channel) {
				try {
					channel.close();
				} catch (TimeoutException | IOException e) {
					log.error("关闭 Channel 时出错：{}", e);
				}
			}

			if (null != connection) {
				try {
					connection.close();
				} catch (IOException e) {
					log.error("关闭 Connection 时出错：{}", e);
				}
			}
		}
	}
}
