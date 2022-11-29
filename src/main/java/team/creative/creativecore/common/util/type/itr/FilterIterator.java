package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilterIterator<T> implements Iterator<T> {
    
    private final Predicate predicate;
    private final Iterator itr;
    private T next;
    
    public FilterIterator(Iterable iterable, Class clazz) {
        this(iterable.iterator(), clazz);
    }
    
    public FilterIterator(Iterator iterable, Class clazz) {
        this(iterable, x -> clazz.isInstance(x));
    }
    
    public <E> FilterIterator(Iterable<E> iterable, Predicate<E> predicate) {
        this(iterable.iterator(), predicate);
    }
    
    public <E> FilterIterator(Iterator<E> iterator, Predicate<E> predicate) {
        this.itr = iterator;
        this.predicate = predicate;
        findNext();
    }
    
    private void findNext() {
        while (next == null && itr.hasNext()) {
            Object object = itr.next();
            if (predicate.test(object)) {
                next = (T) object;
                return;
            }
        }
        next = null;
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    @Override
    public T next() {
        T result = next;
        next = null;
        findNext();
        return result;
    }
    
}
