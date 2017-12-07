package com.hebangdata.ra.vos;

import com.hebangdata.ra.consts.Parameter;
import com.hebangdata.ra.utils.SerializableUtils;

import java.io.*;

/**
 * 任务 A 的参数，可以分解为更小的参数
 */
public class TaskAParameter implements Parameter {
	private final static String EXCHANGER_NAME = "task_a_x";

	private final String[] article;

	public int getArticleLength() {
		return article.length;
	}
	public String[] getSubArticle(final int begin, final int length) {
		if (begin < 0) throw new IllegalArgumentException("begin 参数范围错误");
		if (length < 1) throw new IllegalArgumentException("length 参数范围错误");
		if (begin >= article.length) throw new IllegalArgumentException("begin 超出范围");

		final int originSize = article.length;
		final int targetSize = begin + length;
		final int size = originSize >= targetSize ? targetSize : originSize;

		final String[] results = new String[size];
//		System.arraycopy(article, begin, results, 0, size);
		for (int i = begin; i<size; i++) {
			results[i-begin] = article[i];
		}

		return results;
	}

	public TaskAParameter(final String articlePath) throws IOException {
		final InputStream is = new FileInputStream(articlePath);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		final int size = (int)reader.lines().count();

		article = new String[size];

		String line = reader.readLine();
		int i = 0;
		while (null != line) {
			if (line.length() > 0) article[i] = line;

			line = reader.readLine();
		}

		reader.close();
		is.close();
	}

	public TaskAParameter(final String[] article) {
		this.article = article;
	}

	@Override
	public String getExchangerName() {
		return EXCHANGER_NAME;
	}

	@Override
	public byte[] getBytes() {
		return SerializableUtils.getBytes(this);
	}
}
