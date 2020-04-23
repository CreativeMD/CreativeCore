package com.creativemd.creativecore.common.utils.type;

import java.util.ArrayList;
import java.util.List;

public class TickQueue {
	
	private int tick;
	private int queueLength;
	private List<Runnable>[] queue;
	private LongQueue longQueue;
	private int index;
	
	public TickQueue(int queueLength) {
		setQueueLength(queueLength);
	}
	
	public void setQueueLength(int length) {
		List<Runnable>[] old = queue;
		int indexBefore = index;
		LongQueue oldLongQueue = longQueue;
		
		this.queue = new List[length];
		this.queueLength = length;
		for (int i = 0; i < queue.length; i++)
			queue[i] = new ArrayList<>();
		this.longQueue = null;
		this.index = 0;
		
		if (old != null) {
			for (int i = 0; i < old.length; i++)
				queue(i + 1, old[(i + indexBefore) % old.length]);
			
			LongQueue current = longQueue;
			while (current != null) {
				queue(current.enterTick - tick, current.run);
				current = current.next;
			}
		}
	}
	
	public void tick() {
		tick++;
		
		List<Runnable> runs = queue[index];
		if (!runs.isEmpty())
			for (int i = 0; i < runs.size(); i++)
				runs.get(i).run();
			
		int lastIndex = index;
		index = (index + 1) % queueLength;
		
		LongQueue current = longQueue;
		while (current.enterTick <= tick) {
			queue[lastIndex].add(current.run);
			
			if (current.previous != null)
				current.previous.next = null;
			current.previous = null;
			current = current.next;
		}
		
		longQueue = current;
		longQueue.previous = null;
	}
	
	public void queue(int waitTicks, Runnable run) {
		if (waitTicks == 0)
			run.run();
		else if (waitTicks > queueLength)
			queueLong(waitTicks, run);
		else {
			int tick = waitTicks - 1;
			int index = (this.index + tick - 1) % queueLength;
			queue[index].add(run);
		}
	}
	
	public void queue(int waitTicks, List<Runnable> run) {
		if (waitTicks == 0) {
			for (int i = 0; i < run.size(); i++)
				run.get(i).run();
		} else if (waitTicks > queueLength)
			for (int i = 0; i < run.size(); i++)
				queueLong(waitTicks, run.get(i));
		else {
			int tick = waitTicks - 1;
			int index = (this.index + tick - 1) % queueLength;
			queue[index].addAll(run);
		}
	}
	
	protected void queueLong(int waitTicks, Runnable run) {
		LongQueue entry = new LongQueue(waitTicks, run);
		
		LongQueue previous = null;
		LongQueue current = longQueue;
		
		while (current != null && current.enterTick < entry.enterTick) {
			previous = current;
			current = current.next;
		}
		if (previous != null)
			previous.next = entry;
		else
			longQueue = entry;
		
		entry.previous = previous;
		entry.next = current;
		
		if (current != null)
			current.previous = entry;
	}
	
	class LongQueue {
		
		public final int enterTick;
		public final Runnable run;
		
		public LongQueue next;
		public LongQueue previous;
		
		public LongQueue(int waitTicks, Runnable run) {
			this.enterTick = tick + waitTicks;
			this.run = run;
		}
		
	}
	
}
