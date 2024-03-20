package team.creative.creativecore.common.util.type.list;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class MarkList<T> implements Iterable<T> {
    
    public static final MarkList EMPTY = new MarkList<>(Collections.EMPTY_LIST);
    
    private final List<T> content;
    private final BitSet set;
    private int remaining;
    
    public MarkList(List<T> content) {
        this.content = content;
        this.set = new BitSet(content.size());
        this.remaining = content.size();
    }
    
    public void mark(int index) {
        if (!set.get(index))
            remaining--;
        set.set(index, true);
    }
    
    public void reset(int index) {
        if (set.get(index))
            remaining++;
        set.set(index, false);
    }
    
    public boolean is(int index) {
        return set.get(index);
    }
    
    public int remaing() {
        return remaining;
    }
    
    public boolean isEmpty() {
        return remaining <= 0;
    }
    
    @Override
    public MarkIterator<T> iterator() {
        return new MarkIterator<T>() {
            
            int next = 0;
            int last = 0;
            boolean found = false;
            
            @Override
            public boolean hasNext() {
                while (!found && next < content.size()) {
                    if (!set.get(next))
                        found = true;
                    else
                        next++;
                }
                return found;
            }
            
            @Override
            public T next() {
                last = next;
                found = false;
                next++;
                return content.get(last);
            }
            
            @Override
            public void mark() {
                if (!set.get(last))
                    remaining--;
                set.set(last);
            }
            
        };
    }
    
    public void clear() {
        set.clear();
        remaining = content.size();
    }
    
    public Iterable<T> allIgnoreMark() {
        return content;
    }
    
    public T getIgnoreMark(int index) {
        return content.get(index);
    }
    
    public int sizeIgnoreMark() {
        return content.size();
    }
    
}
