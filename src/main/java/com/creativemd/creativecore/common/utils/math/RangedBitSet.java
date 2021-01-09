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
    
    public void add(int value) {
        if (value >= min && value <= max)
            set.set(value - min, true);
    }
    
    public List<BitRange> getRanges() {
        List<BitRange> ranges = new ArrayList<>();
        int index = 0;
        while (index < max - min) {
            int nextIndex = set.nextSetBit(index + 1);
            if (nextIndex == -1)
                nextIndex = max - min;
            
            ranges.add(new BitRange(index + min, nextIndex + min));
            
            index = nextIndex;
        }
        return ranges;
    }
    
    public static class BitRange {
        
        public final int min;
        public final int max;
        
        public BitRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
        
    }
    
}
