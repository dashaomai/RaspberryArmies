package com.hebangdata.ra.consts;

/**
 * 通讯相关的常量定义
 */
public class Commucation {
	// 任务 A 的 Exchanger 名
	public final static String TASK_A_EXCHANGER = "task_a_x";
	public final static String TASK_A_QUEUE = "task_a_q";

	// 熊大分词任务的 Exchanger 名
	public final static String SENTENCE_SPLIT_EXCHANGER = "sentence_split_x";
	public final static String SENTENCE_SPLIT_QUEUE = "sentence_split_q";

	public final static String SENTENCE_SPLIT_ROUTING_KEY_A = "sentence.split.a";
	public final static String SENTENCE_SPLIT_ROUTING_KEY_SOLDIER = "sentence.split.#";
}
