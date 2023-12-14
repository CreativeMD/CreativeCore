package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.Objects;

public class NestedIterator<T> implements IterableIterator<T> {
    
    public final Iterator<? extends Iterable<T>> itr;
    protected Iterator<T> current;
    
    public NestedIterator(Iterator<? extends Iterable<T>> itrs) {
        Objects.requireNonNull(itrs);
        this.itr = itrs;
    }
    
    public NestedIterator(Iterable<? extends Iterable<T>> itrs) {
        Objects.requireNonNull(itrs);
        this.itr = itrs.iterator();
    }
    
    @Override
    public boolean hasNext() {
        while ((current == null || !current.hasNext()) && itr.hasNext())
            current = itr.next().iterator();
        return current != null && current.hasNext();
    }
    
    @Override
    public T next() {
        return current.next();
    }
    
}
