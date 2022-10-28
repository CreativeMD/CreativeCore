package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;

public class InverseConsecutiveIterator<T> implements Iterator<T> {
    
    public final Iterator<T>[] itrs;
    protected int index;
    
    public InverseConsecutiveIterator(Iterator<T>... itrs) {
        this.itrs = itrs;
        this.index = this.itrs.length - 1;
    }
    
    public InverseConsecutiveIterator(Iterable<T>... itrs) {
        this(new Iterator[itrs.length]);
        for (int i = 0; i < itrs.length; i++)
            this.itrs[i] = itrs[i].iterator();
    }
    
    @Override
    public boolean hasNext() {
        while (index >= 0 && !itrs[index].hasNext())
            index--;
        return index >= 0;
    }
    
    @Override
    public T next() {
        return itrs[index].next();
    }
    
}
