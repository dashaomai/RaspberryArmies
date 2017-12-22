package com.hebangdata.ra.sentenceSplit;

import com.hebangdata.ra.consts.Commucation;
import com.hebangdata.ra.utils.SerializableUtils;
import com.hebangdata.ra.vos.SentenceSplitParameter;
import com.hebangdata.ra.vos.SentenceSplitResponse;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SentenceSplitSoldier {
	private final static Logger log = LoggerFactory.getLogger("SentenceSplitSoldier");

	private Connection connection;
	private Channel channel;

	public SentenceSplitSoldier(final String host) throws IOException, TimeoutException {
		final ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);

		connection = factory.newConnection();
		channel = connection.createChannel();

		channel.basicQos(1);
		channel.exchangeDeclare(Commucation.SENTENCE_SPLIT_EXCHANGER, BuiltinExchangeType.TOPIC, true);

		// 连接到同一个队列，来实现任务的自动分配
		final String queueName = channel.queueDeclare(Commucation.SENTENCE_SPLIT_QUEUE, true, false, false, null).getQueue();
		channel.queueBind(queueName, Commucation.SENTENCE_SPLIT_EXCHANGER, Commucation.SENTENCE_SPLIT_ROUTING_KEY_SOLDIER);

		channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				// 收到消息，把输入参数转换出来
				final SentenceSplitParameter parameter = SerializableUtils.getObject(body);

				// 打印 task.a
				log.info(envelope.getRoutingKey());

				// 生成对应的返回参数
				final SentenceSplitResponse response = calculate(parameter);

				if (null == response) {
					log.warn("无法转换参数：{} 成为对应的结果", parameter);
				} else {
					final AMQP.BasicProperties replyProps = new AMQP.BasicProperties()
							.builder()
							.correlationId(properties.getCorrelationId())
							.build();

					channel.basicPublish("", properties.getReplyTo(), true, replyProps, SerializableUtils.getBytes(response));
				}

				// 以应答表示处理完成
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		});
	}

	private SentenceSplitResponse calculate(final SentenceSplitParameter parameter) {
		if (null == parameter) return null;

		// 语句拆分
		final List<String> lines = new ArrayList<>();

		for (final String line : parameter.lines) {
			if (null == line) continue;

			// 循环每一个字符，拆出每一个完整的句子
			final StringBuilder builder = new StringBuilder();

			for (final Character chr : line.toCharArray()) {
				if (isSpliter(chr)) {
					if (0 < builder.length()) {
						lines.add(builder.toString());
						builder.delete(0, builder.length());
					}
				} else {
					builder.append(chr);
				}
			}
		}

		// 伪代码，先不做计算，直接返回空结果
		final String[] sentences = new String[lines.size()];
		lines.toArray(sentences);

		return new SentenceSplitResponse(parameter.filePath, sentences);
	}

	/**
	 * 判断一个字符是不是非句内字符
	 * @param chr
	 * @return
	 */
	private static boolean isSpliter(final Character chr) {
		// 判断是不是汉字
		final UnicodeScript ub = UnicodeScript.of(chr);

		if (UnicodeScript.HAN == ub || UnicodeScript.LATIN == ub)
			return false;
		else
			return 0 < chr.compareTo('0') || 0 > chr.compareTo('9');
	}
}
