package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilterIterator<T> extends ComputeNextIterator<T> {
    
    public static <T> FilterIterator<T> skipNull(Iterator<T> itr) {
        return new FilterIterator<>(itr, x -> x != null);
    }
    
    private final Predicate predicate;
    private final Iterator itr;
    
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
    }
    
    @Override
    protected T computeNext() {
        while (itr.hasNext()) {
            Object object = itr.next();
            if (predicate.test(object)) {
                return (T) object;
            }
        }
        return end();
    }
    
}
