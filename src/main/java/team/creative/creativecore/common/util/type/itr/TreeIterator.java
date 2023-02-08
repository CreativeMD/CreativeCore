package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.function.Function;

public class TreeIterator<T> implements Iterator<T> {
    
    private Function<T, Iterator<T>> function;
    private T start;
    private Iterator<T> itr;
    private TreeIterator<T> current;
    
    public TreeIterator(T start, Function<T, Iterator<T>> function) {
        this.start = start;
        this.function = function;
        this.itr = function.apply(start);
    }
    
    @Override
    public boolean hasNext() {
        if (start != null)
            return true;
        if (current != null && current.hasNext())
            return true;
        return itr.hasNext();
    }
    
    @Override
    public T next() {
        if (start != null) {
            T next = start;
            start = null;
            return next;
        }
        if (current != null && current.hasNext())
            return current.next();
        
        current = new TreeIterator<>(itr.next(), function);
        return current.next();
    }
    
}
