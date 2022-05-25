package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class SingleIterator<E> implements Iterator<E> {
    
    private E element;
    
    public SingleIterator(E element) {
        this.element = element;
    }
    
    @Override
    public boolean hasNext() {
        return element != null;
    }
    
    @Override
    public E next() {
        if (element != null) {
            E result = element;
            element = null;
            return result;
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        if (element != null) {
            action.accept(element);
            element = null;
        }
    }
}
