package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class NestedFunctionIterator<T> implements IterableIterator<T> {
    
    public final Iterator itr;
    public final Function function;
    protected Iterator<T> current;
    
    public <V> NestedFunctionIterator(Iterable<V> itrs, Function<V, Iterable<T>> func) {
        Objects.requireNonNull(itrs);
        Objects.requireNonNull(func);
        this.itr = itrs.iterator();
        this.function = func;
    }
    
    public <V> NestedFunctionIterator(Iterator<V> itrs, Function<V, Iterable<T>> func) {
        Objects.requireNonNull(itrs);
        Objects.requireNonNull(func);
        this.itr = itrs;
        this.function = func;
    }
    
    @Override
    public boolean hasNext() {
        while ((current == null || !current.hasNext()) && itr.hasNext())
            current = ((Iterable<T>) function.apply(itr.next())).iterator();
        return current != null && current.hasNext();
    }
    
    @Override
    public T next() {
        return current.next();
    }
    
}
