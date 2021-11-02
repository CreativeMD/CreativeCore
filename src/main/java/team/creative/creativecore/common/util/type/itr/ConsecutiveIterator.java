package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;

public class ConsecutiveIterator<T> implements Iterator<T> {
    
    public final Iterator<T>[] itrs;
    protected int index;
    
    public ConsecutiveIterator(Iterator<T>... itrs) {
        this.itrs = itrs;
    }
    
    public ConsecutiveIterator(Iterable<T>... itrs) {
        this.itrs = new Iterator[itrs.length];
        for (int i = 0; i < itrs.length; i++)
            this.itrs[i] = itrs[i].iterator();
    }
    
    @Override
    public boolean hasNext() {
        while (index < itrs.length && !itrs[index].hasNext())
            index++;
        return index < itrs.length;
    }
    
    @Override
    public T next() {
        return itrs[index].next();
    }
    
}
