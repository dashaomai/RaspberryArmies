package com.hebangdata.ra.utils;

import com.hebangdata.ra.vos.TaskAResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SerializableUtils {
	private final static Logger log = LoggerFactory.getLogger("SerializableUtils");

	public static byte[] getBytes(final Object object) {
		final ByteArrayOutputStream buff = new ByteArrayOutputStream();
		try {
			final ObjectOutputStream output = new ObjectOutputStream(buff);
			output.writeObject(object);

			return buff.toByteArray();
		} catch (IOException e) {
			log.error("转换对象为字节流时出错：{}", e);

			return null;
		}
	}

	public static <T> T getObject(final byte[] bytes) {

		final ObjectInputStream input;
		try {
			input = new ObjectInputStream(new ByteArrayInputStream(bytes));
			final T response = (T) input.readObject();

			return response;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();

			return null;
		}
	}
}
