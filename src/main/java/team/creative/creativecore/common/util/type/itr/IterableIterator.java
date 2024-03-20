package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;

public interface IterableIterator<T> extends Iterable<T>, Iterator<T> {
    
    @Override
    default Iterator<T> iterator() {
        return this;
    }
    
}
