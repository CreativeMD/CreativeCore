package team.creative.creativecore.common.util.type.set;

import java.util.BitSet;

public class BiBitSet {
    
    protected BitSet postive;
    protected BitSet negative;
    
    public BiBitSet() {
        this.postive = new BitSet();
        this.negative = new BitSet();
    }
    
    public BiBitSet(BiBitSet set) {
        this.postive = (BitSet) set.postive.clone();
        this.negative = (BitSet) set.negative.clone();
    }
    
    public void flip(int bitIndex) {
        if (bitIndex >= 0)
            postive.flip(bitIndex);
        else
            negative.flip(-1 - bitIndex);
    }
    
    public void flip(int fromIndex, int toIndex) {
        if (fromIndex >= 0)
            postive.flip(fromIndex, toIndex);
        else if (toIndex >= 0) {
            negative.flip(0, -1 - fromIndex);
            postive.flip(0, toIndex);
        } else
            negative.flip(-1 - toIndex, -1 - fromIndex);
    }
    
    public void set(int bitIndex) {
        if (bitIndex >= 0)
            postive.set(bitIndex);
        else
            negative.set(-1 - bitIndex);
    }
    
    public void set(int bitIndex, boolean value) {
        if (bitIndex >= 0)
            postive.set(bitIndex, value);
        else
            negative.set(-1 - bitIndex, value);
    }
    
    public void set(int fromIndex, int toIndex) {
        if (fromIndex >= 0)
            postive.set(fromIndex, toIndex);
        else if (toIndex >= 0) {
            negative.set(0, -1 - fromIndex);
            postive.set(0, toIndex);
        } else
            negative.set(-1 - toIndex, -1 - fromIndex);
    }
    
    public void set(int fromIndex, int toIndex, boolean value) {
        if (fromIndex >= 0)
            postive.set(fromIndex, toIndex, value);
        else if (toIndex >= 0) {
            negative.set(0, -1 - fromIndex, value);
            postive.set(0, toIndex, value);
        } else
            negative.set(-1 - toIndex, -1 - fromIndex, value);
    }
    
    public void clear(int bitIndex) {
        if (bitIndex >= 0)
            postive.clear(bitIndex);
        else
            negative.clear(-1 - bitIndex);
    }
    
    public void clear(int fromIndex, int toIndex) {
        if (fromIndex >= 0)
            postive.clear(fromIndex, toIndex);
        else if (toIndex >= 0) {
            negative.clear(0, -1 - fromIndex);
            postive.clear(0, toIndex);
        } else
            negative.clear(-1 - toIndex, -1 - fromIndex);
    }
    
    public void clear() {
        postive.clear();
        negative.clear();
    }
    
    public boolean get(int bitIndex) {
        if (bitIndex >= 0)
            return postive.get(bitIndex);
        else
            return negative.get(-1 - bitIndex);
    }
    
    public BitSet get(int fromIndex, int toIndex) {
        if (fromIndex >= 0)
            return postive.get(fromIndex, toIndex);
        else if (toIndex >= 0) {
            BitSet result = new BitSet(Math.abs(fromIndex) + toIndex + 1);
            int index = 0;
            for (int i = fromIndex; i < 0; i++) {
                result.set(index, negative.get(-1 - i));
                index++;
            }
            for (int i = 0; i <= toIndex; i++) {
                result.set(index, postive.get(i));
                index++;
            }
            return result;
        } else {
            BitSet result = new BitSet(Math.abs(fromIndex - toIndex));
            int index = 0;
            for (int i = fromIndex; i <= toIndex; i++) {
                result.set(index, negative.get(-1 - i));
                index++;
            }
            return result;
        }
    }
    
    public boolean isEmpty() {
        return postive.isEmpty() && negative.isEmpty();
    }
    
    public boolean intersects(BiBitSet set) {
        return postive.intersects(set.postive) || negative.intersects(set.negative);
    }
    
    public void and(BiBitSet set) {
        postive.and(set.postive);
        negative.and(set.negative);
    }
    
    public void or(BiBitSet set) {
        postive.or(set.postive);
        negative.or(set.negative);
    }
    
    public void xor(BiBitSet set) {
        postive.xor(set.postive);
        negative.xor(set.negative);
    }
    
    public void andNot(BiBitSet set) {
        postive.andNot(set.postive);
        negative.andNot(set.negative);
    }
    
    @Override
    public int hashCode() {
        return postive.hashCode() + negative.hashCode();
    }
    
    public int count() {
        int count = 0;
        for (int i = 0; i < postive.length(); i++)
            if (postive.get(i))
                count++;
        for (int i = 0; i < negative.length(); i++)
            if (negative.get(i))
                count++;
        return count;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BiBitSet)
            return ((BiBitSet) obj).postive.equals(postive) && ((BiBitSet) obj).negative.equals(negative);
        return false;
    }
    
    @Override
    public Object clone() {
        return new BiBitSet(this);
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        for (int i = negative.length() - 1; i >= 0; i--)
            if (negative.get(i)) {
                if (first)
                    first = false;
                else
                    result.append(", ");
                result.append(-i - 1);
            }
        for (int i = 0; i < postive.length(); i++)
            if (postive.get(i)) {
                if (first)
                    first = false;
                else
                    result.append(", ");
                result.append(i);
            }
        result.append("}");
        return result.toString();
    }
    
}
