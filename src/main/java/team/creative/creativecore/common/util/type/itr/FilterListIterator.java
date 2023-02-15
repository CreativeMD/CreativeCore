package team.creative.creativecore.common.util.type.itr;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

public class FilterListIterator<T> extends ComputeNextListIterator<T> {
    
    public static <T> FilterListIterator<T> skipNull(ListIterator<T> itr) {
        return new FilterListIterator<>(itr, x -> x != null);
    }
    
    private final Predicate<T> predicate;
    private final ListIterator<T> itr;
    
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
    }
    
    @Override
    protected T computeNext() {
        while (itr.hasNext()) {
            T object = itr.next();
            if (predicate.test(object))
                return object;
        }
        return endNext();
    }
    
    @Override
    protected T computePrevious() {
        while (itr.hasPrevious()) {
            T object = itr.previous();
            if (predicate.test(object))
                return object;
        }
        return endPrevious();
    }
    
    @Override
    public int nextIndex() {
        return itr.nextIndex();
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
