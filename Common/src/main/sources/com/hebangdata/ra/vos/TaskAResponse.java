package com.hebangdata.ra.vos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TaskAResponse implements Serializable {
	public final Map<Character, Long> statistics = new HashMap<>();
}
