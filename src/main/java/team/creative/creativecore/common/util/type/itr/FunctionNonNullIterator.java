package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class FunctionNonNullIterator<T> extends ComputeNextIterator<T> {
    
    protected final Iterator itr;
    protected final Function convert;
    
    public <V> FunctionNonNullIterator(Iterator<V> itr, Function<V, T> func) {
        Objects.requireNonNull(itr);
        Objects.requireNonNull(func);
        this.itr = itr;
        this.convert = func;
    }
    
    public <V> FunctionNonNullIterator(Iterable<V> itr, Function<V, T> func) {
        Objects.requireNonNull(itr);
        Objects.requireNonNull(func);
        this.itr = itr.iterator();
        this.convert = func;
    }
    
    @Override
    protected T computeNext() {
        while (itr.hasNext()) {
            Object object = convert.apply(itr.next());
            if (object != null)
                return (T) object;
        }
        return end();
    }
    
}
