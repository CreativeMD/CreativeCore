package com.creativemd.creativecore.common.utils.math;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RangedBitSet {
	
	protected BitSet set;
	protected int min;
	protected int max;
	
	public RangedBitSet(int min, int max) {
		this.min = min;
		this.max = max;
		if(min >= max)
			throw new IllegalArgumentException("min has to be smaller than max!");
		this.set = new BitSet(max - min);
	}
	
	public int getMin()
	{
		return min;
	}
	
	public int getMax()
	{
		return max;
	}
	
	public void set(int min, int max)
	{
		set(min, max, true);
	}
	
	public void set(int min, int max, boolean value)
	{
		int min_index = Math.max(this.min, min);
		int max_index = Math.min(this.max, max);
		
		if(min_index < this.max)
			set.set(min_index - this.min, max_index - this.min, value);
	}
	
	public List<BitRange> getRanges()
	{
		List<BitRange> ranges = new ArrayList<>();
		int index = 0;
		while(index < max - min)
		{
			boolean value = set.get(index);
			int nextIndex = value ? set.nextClearBit(index) : set.nextSetBit(index);
			if(nextIndex == -1)
				nextIndex = max - min;
			
			ranges.add(new BitRange(index + min, nextIndex + min, value));
			
			index = nextIndex;
		}
		return ranges;
	}
	
	public static class BitRange {
		
		public final int min;
		public final int max;
		public final boolean value;
		
		public BitRange(int min, int max, boolean value)
		{
			this.min = min;
			this.max = max;
			this.value = value;
		}
		
	}

}
