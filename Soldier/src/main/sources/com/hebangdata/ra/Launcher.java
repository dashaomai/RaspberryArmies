package com.hebangdata.ra;

import com.hebangdata.ra.sentenceSplit.SentenceSplitSoldier;
import com.hebangdata.ra.taskA.TaskASoldier;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Launcher {
	public static void main(String[] args) throws IOException, TimeoutException {
		new SentenceSplitSoldier("127.0.0.1");
	}
}
