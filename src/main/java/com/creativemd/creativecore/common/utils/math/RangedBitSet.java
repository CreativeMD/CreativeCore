package com.creativemd.creativecore.common.utils.math;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class RangedBitSet {
	
	protected BitSet set;
	protected int min;
	protected int max;
	
	public RangedBitSet(int min, int max) {
		this.min = min;
		this.max = max;
		if (min >= max)
			throw new IllegalArgumentException("min has to be smaller than max!");
		this.set = new BitSet(max - min);
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void add(int min, int max) {
		int start = Math.max(this.min, min);
		int end = Math.min(this.max, max);
		if (start <= end)
			set.set(start - this.min, end - this.min, true);
	}
	
	public List<BitRange> getRanges() {
		boolean inside = set.get(0);
		List<BitRange> ranges = new ArrayList<>();
		int index = 0;
		while (index < max - min) {
			int nextIndex = inside ? set.nextClearBit(index + 1) : set.nextSetBit(index + 1);
			if (nextIndex == -1)
				nextIndex = max - min;
			
			ranges.add(new BitRange(index + min, nextIndex + min, inside));
			
			index = nextIndex;
			inside = !inside;
		}
		return ranges;
	}
	
	public static class BitRange {
		
		public final int min;
		public final int max;
		public final boolean inside;
		
		public BitRange(int min, int max, boolean inside) {
			this.min = min;
			this.max = max;
			this.inside = inside;
		}
		
	}
	
}
