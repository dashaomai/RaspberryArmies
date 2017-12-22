package com.hebangdata.ra.vos;

import com.hebangdata.ra.consts.Commucation;
import com.hebangdata.ra.consts.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 拆分语句的参数对象
 */
public class SentenceSplitParameter implements Parameter {
	private static final Logger log = LoggerFactory.getLogger("SentenceSplitParameter");

	public final String filePath;

	public final String[] lines;

	public String[] getSubLines(final int begin, final int length) {
		if (begin < 0) throw new IllegalArgumentException("begin 参数范围错误");
		if (length < 1) throw new IllegalArgumentException("length 参数范围错误");
		if (begin >= lines.length) throw new IllegalArgumentException("begin 超出范围");

		final int originSize = lines.length;
		final int targetSize = begin + length;
		final int size = originSize >= targetSize ? targetSize : originSize;

		final String[] results = new String[size];
		for (int i = begin; i<size; i++) {
			results[i-begin] = lines[i];
		}

		return results;
	}

	public SentenceSplitParameter(final String filePath) throws IOException {
		this.filePath = filePath;

		final InputStream is = new FileInputStream(filePath);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		final List<String> buff = new ArrayList<>();

		String line = reader.readLine();
		while (null != line) {
			if (line.length() > 0) buff.add(line);

			line = reader.readLine();
		}

		lines = new String[buff.size()];
		buff.toArray(lines);

		log.info("处理文件：{}，获得 {} 行文本", filePath, lines.length);

		reader.close();
		is.close();
	}

	public SentenceSplitParameter(final String filePath, final String[] lines) {
		this.filePath = filePath;
		this.lines = lines;
	}

	@Override
	public String getExchangerName() {
		return Commucation.SENTENCE_SPLIT_EXCHANGER;
	}
}
