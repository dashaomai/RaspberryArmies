package com.hebangdata.ra.vos;

import com.hebangdata.ra.consts.Commucation;
import com.hebangdata.ra.consts.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务 A 的参数，可以分解为更小的参数
 */
public class TaskAParameter implements Parameter {
	private final String[] article;

	public String[] getArticle() {
		return article;
	}

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

		final List<String> buff = new ArrayList<>();

		String line = reader.readLine();
		while (null != line) {
			if (line.length() > 0) buff.add(line);

			line = reader.readLine();
		}

		article = new String[buff.size()];
		buff.toArray(article);

		reader.close();
		is.close();
	}

	public TaskAParameter(final String[] article) {
		this.article = article;
	}

	@Override
	public String getExchangerName() {
		return Commucation.TASK_A_EXCHANGER;
	}
}
