package com.creativemd.creativecore.common.gui.controls.gui.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;

public abstract class TimelineChannel<T> {
	
	public int index;
	public String name;
	public List<KeyControl<T>> controls = new ArrayList<>();
	
	public TimelineChannel(String name) {
		this.name = name;
	}
	
	public TimelineChannel addKeys(PairList<Integer, T> keys) {
		if (keys == null || keys.isEmpty())
			return this;
		for (Pair<Integer, T> pair : keys) {
			addKey(pair.key, pair.value);
		}
		return this;
	}
	
	public TimelineChannel<T> addKeyFixed(int tick, T value) {
		KeyControl control = this.addKey(tick, value);
		control.modifiable = false;
		return this;
	}
	
	public KeyControl addKey(int tick, T value) {
		KeyControl control = new KeyControl(this, controls.size(), tick, value);
		for (int i = 0; i < controls.size(); i++) {
			KeyControl other = controls.get(i);
			
			if (other.tick == tick)
				return null;
			
			if (other.tick > tick) {
				controls.add(i, control);
				return control;
			}
		}
		controls.add(control);
		return control;
	}
	
	public void removeKey(KeyControl control) {
		controls.remove(control);
	}
	
	public void movedKey(KeyControl control) {
		Collections.sort(controls);
	}
	
	public boolean isSpaceFor(KeyControl control, int tick) {
		for (int i = 0; i < controls.size(); i++) {
			int otherTick = controls.get(i).tick;
			if (otherTick == tick)
				return false;
			if (otherTick > tick)
				return true;
		}
		return true;
	}
	
	public T getValueAt(int tick) {
		if (controls.isEmpty())
			return getDefault();
		
		int higher = controls.size();
		for (int i = 0; i < controls.size(); i++) {
			int otherTick = controls.get(i).tick;
			if (otherTick == tick)
				return controls.get(i).value;
			if (otherTick > tick) {
				higher = i;
				break;
			}
		}
		
		if (higher == 0 || higher == controls.size())
			return controls.get(higher == 0 ? 0 : controls.size() - 1).value;
		
		KeyControl<T> before = controls.get(higher - 1);
		KeyControl<T> after = controls.get(higher);
		double percentage = (double) (tick - before.tick) / (after.tick - before.tick);
		return getValueAt(before, after, percentage);
	}
	
	protected abstract T getValueAt(KeyControl<T> before, KeyControl<T> after, double percentage);
	
	protected abstract T getDefault();
	
	public PairList<Integer, T> getPairs() {
		if (controls.isEmpty())
			return null;
		boolean fixed = true;
		PairList<Integer, T> list = new PairList<>();
		for (KeyControl<T> control : controls) {
			if (control.modifiable)
				fixed = false;
			list.add(control.tick, control.value);
		}
		if (fixed)
			return null;
		return list;
	}
	
	public static class TimelineChannelInteger extends TimelineChannel<Integer> {
		
		public TimelineChannelInteger(String name) {
			super(name);
		}
		
		@Override
		protected Integer getValueAt(KeyControl<Integer> before, KeyControl<Integer> after, double percentage) {
			int dif = after.value - before.value;
			double current = dif * percentage + (before.value);
			return (int) current;
		}
		
		@Override
		protected Integer getDefault() {
			return 0;
		}
		
	}
	
	public static class TimelineChannelDouble extends TimelineChannel<Double> {
		
		public TimelineChannelDouble(String name) {
			super(name);
		}
		
		@Override
		protected Double getValueAt(KeyControl<Double> before, KeyControl<Double> after, double percentage) {
			return Math.round(((after.value - before.value) * percentage + before.value) * 100D) / 100D;
		}
		
		@Override
		protected Double getDefault() {
			return 0D;
		}
		
	}
}
