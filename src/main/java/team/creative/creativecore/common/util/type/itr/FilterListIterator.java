package team.creative.creativecore.common.util.type.itr;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

public class FilterListIterator<T> implements ListIterator<T> {
    
    public static <T> FilterListIterator<T> skipNull(ListIterator<T> itr) {
        return new FilterListIterator<>(itr, x -> x != null);
    }
    
    private final Predicate<T> predicate;
    private final ListIterator<T> itr;
    private T next;
    private T previous;
    
    public FilterListIterator(List iterable, Class clazz) {
        this(iterable.listIterator(), clazz);
    }
    
    public FilterListIterator(ListIterator iterable, Class clazz) {
        this(iterable, x -> clazz.isInstance(x));
    }
    
    public FilterListIterator(List<T> iterable, Predicate<T> predicate) {
        this(iterable.listIterator(), predicate);
    }
    
    public FilterListIterator(ListIterator<T> iterator, Predicate<T> predicate) {
        this.itr = iterator;
        this.predicate = predicate;
        findNext();
        findPrevious();
    }
    
    private void findNext() {
        while (next == null && itr.hasNext()) {
            T object = itr.next();
            if (predicate.test(object)) {
                next = object;
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
        previous = result;
        findNext();
        return result;
    }
    
    @Override
    public int nextIndex() {
        return itr.nextIndex();
    }
    
    private void findPrevious() {
        while (previous == null && itr.hasPrevious()) {
            T object = itr.previous();
            if (predicate.test(object)) {
                previous = object;
                return;
            }
        }
        previous = null;
    }
    
    @Override
    public boolean hasPrevious() {
        return previous != null;
    }
    
    @Override
    public T previous() {
        T result = previous;
        previous = null;
        next = result;
        findPrevious();
        return result;
    }
    
    @Override
    public int previousIndex() {
        return itr.previousIndex();
    }
    
    @Override
    public void remove() {
        itr.remove();
    }
    
    @Override
    public void set(T e) {
        itr.set(e);
    }
    
    @Override
    public void add(T e) {
        itr.add(e);
    }
    
}
