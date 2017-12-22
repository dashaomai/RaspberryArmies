package com.hebangdata.ra;

import com.hebangdata.ra.sentenceSplit.SentenceSplitOfficer;
import com.hebangdata.ra.taskA.TaskAOfficer;
import com.hebangdata.ra.vos.SentenceSplitParameter;
import com.hebangdata.ra.vos.TaskAParameter;

import java.io.IOException;

public class Launcher {
	public static void main(String[] args) throws IOException {
		// TaskA 类型的测试代码
//		final TaskAOfficer officer = new TaskAOfficer();
//
//		officer.Run("192.168.9.87", new TaskAParameter("择天记.txt"));

		// 熊大的分词功能
		new SentenceSplitOfficer().Run("127.0.0.1", new SentenceSplitParameter("assets/百度买房语料.txt"));
	}
}
