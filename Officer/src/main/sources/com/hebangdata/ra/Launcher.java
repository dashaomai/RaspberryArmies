package com.hebangdata.ra;

import com.hebangdata.ra.taskA.TaskAOfficer;
import com.hebangdata.ra.vos.TaskAParameter;

import java.io.IOException;

public class Launcher {
	public static void main(String[] args) throws IOException {
		final TaskAOfficer officer = new TaskAOfficer();

		officer.Run("192.168.9.87", new TaskAParameter("择天记.txt"));
	}
}
