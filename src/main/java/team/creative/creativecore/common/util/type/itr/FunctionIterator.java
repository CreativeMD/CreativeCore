package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class FunctionIterator<T> implements Iterator<T> {
    
    protected Iterator itr;
    protected Function convert;
    
    public <V> FunctionIterator(Iterator<V> itr, Function<V, T> func) {
        Objects.requireNonNull(itr);
        Objects.requireNonNull(func);
        this.itr = itr;
        this.convert = func;
    }
    
    public <V> FunctionIterator(Iterable<V> itr, Function<V, T> func) {
        Objects.requireNonNull(itr);
        Objects.requireNonNull(func);
        this.itr = itr.iterator();
        this.convert = func;
    }
    
    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }
    
    @Override
    public T next() {
        return (T) convert.apply(itr.next());
    }
    
}
