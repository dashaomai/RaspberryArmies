package com.hebangdata.ra.taskA;

import com.hebangdata.ra.consts.Commucation;
import com.hebangdata.ra.utils.SerializableUtils;
import com.hebangdata.ra.vos.TaskAParameter;
import com.hebangdata.ra.vos.TaskAResponse;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TaskASoldier {
	private final static Logger log = LoggerFactory.getLogger("TaskASoldier");

	private Connection connection;
	private Channel channel;

	public TaskASoldier(final String host) throws IOException, TimeoutException {
		final ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);

		connection = factory.newConnection();
		channel = connection.createChannel();

		channel.basicQos(1);
		channel.exchangeDeclare(Commucation.TASK_A_EXCHANGER, BuiltinExchangeType.TOPIC, true);

		final String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, Commucation.TASK_A_EXCHANGER, "task.*");

		channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				// 收到消息，把输入参数转换出来
				final TaskAParameter parameter = SerializableUtils.getObject(body);

				// 生成对应的返回参数
				final TaskAResponse response = calculate(parameter);

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

	private TaskAResponse calculate(final TaskAParameter parameter) {
		if (null == parameter) return null;

		final TaskAResponse response = new TaskAResponse();

		// 伪代码，先不做计算，直接返回空结果
		return response;
	}
}
