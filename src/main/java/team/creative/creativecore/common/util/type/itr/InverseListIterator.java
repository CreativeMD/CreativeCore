package team.creative.creativecore.common.util.type.itr;

import java.util.Iterator;
import java.util.List;

public class InverseListIterator<T> implements Iterator<T> {
    
    public final List<T> content;
    private int index;
    
    public InverseListIterator(List<T> content) {
        this.content = content;
        this.index = content.size() - 1;
    }
    
    @Override
    public boolean hasNext() {
        return index >= 0;
    }
    
    @Override
    public T next() {
        T result = content.get(index);
        index--;
        return result;
    }
    
}
