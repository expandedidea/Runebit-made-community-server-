/*
* @ Author - Digistr
* @ Objective - Perfect For Executing Tasks' We only need once in awhile.
*   Do not use as a replacement for tasks that go every cycle it's slower!!!
*/
package com.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class TaskQueue {

	private static final Deque<Task> QUEUED_TASKS = new ArrayDeque<Task>();
	
	public static void add(Task tc) {
		QUEUED_TASKS.add(tc);
	}

	public static void queue() {
		Task tc = null;
		int size = QUEUED_TASKS.size();
		//System.out.print("Queue Size (BEFORE): " + size);
		long t1 = System.nanoTime();
		for (int i = 0; i < size; i++) {
			tc = QUEUED_TASKS.poll();
			if (tc.isDead()) {
				tc.curCycle = 0;
				continue;
			}
			if (tc.canExecute()) {
				tc.execute();
				if (tc.REPEAT) {
					QUEUED_TASKS.addLast(tc);
					tc.reset();
				}
			} else {
				QUEUED_TASKS.addLast(tc);
			}
		}
		long t2 = System.nanoTime();
		//System.out.println("  ,  Queue Size (AFTER): " + QUEUED_TASKS.size() + " Time: " + (t2-t1));
	}
}