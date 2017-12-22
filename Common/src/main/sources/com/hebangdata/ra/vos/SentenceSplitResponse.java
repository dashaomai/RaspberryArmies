package com.hebangdata.ra.vos;

import java.io.Serializable;

public class SentenceSplitResponse implements Serializable {
	public final String filePath;
	public final String[] sentences;

	public SentenceSplitResponse(
			final String filePath,
			final String[] sentences
	) {
		this.filePath = filePath;
		this.sentences = sentences;
	}

	@Override
	public String toString() {
		return "SentenceSplitResponse#{filePath: " + filePath + ", sentences: " + sentences.length + " String(s)}";
	}
}
