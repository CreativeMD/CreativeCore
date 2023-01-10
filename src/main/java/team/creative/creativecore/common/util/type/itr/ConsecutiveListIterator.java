package team.creative.creativecore.common.util.type.itr;

import java.util.List;
import java.util.ListIterator;

public class ConsecutiveListIterator<T> implements ListIterator<T> {
    
    public final ListIterator<T>[] itrs;
    protected int index;
    
    public ConsecutiveListIterator(ListIterator<T>... itrs) {
        this.itrs = itrs;
    }
    
    public ConsecutiveListIterator(List<List<T>> nestedList) {
        this.itrs = new ListIterator[nestedList.size()];
        for (int i = 0; i < itrs.length; i++)
            itrs[i] = nestedList.get(i).listIterator();
    }
    
    public ConsecutiveListIterator goEnd() {
        while (this.hasNext())
            next();
        return this;
    }
    
    @Override
    public boolean hasNext() {
        while (index < itrs.length && !itrs[index].hasNext())
            index++;
        return index < itrs.length;
    }
    
    @Override
    public T next() {
        return itrs[index].next();
    }
    
    @Override
    public boolean hasPrevious() {
        while (index >= 0 && !itrs[index].hasPrevious())
            index--;
        return index >= 0;
    }
    
    @Override
    public T previous() {
        return itrs[index].previous();
    }
    
    @Override
    public int nextIndex() {
        return itrs[index].nextIndex();
    }
    
    @Override
    public int previousIndex() {
        return itrs[index].previousIndex();
    }
    
    @Override
    public void remove() {
        itrs[index].remove();
    }
    
    @Override
    public void set(T e) {
        itrs[index].set(e);
    }
    
    @Override
    public void add(T e) {
        itrs[index].add(e);
    }
    
}
