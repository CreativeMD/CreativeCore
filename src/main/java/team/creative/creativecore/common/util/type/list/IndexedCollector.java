package team.creative.creativecore.common.util.type.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;
import team.creative.creativecore.common.util.type.itr.FilterIterator;

public class IndexedCollector<T> implements Iterable<T> {
    
    private static final Predicate PREDICATE = x -> x.getClass() != Integer.class;
    private final List content = new ArrayList();
    private int count;
    private int lastIndex;
    private boolean sectionStarted = false;
    
    public void add(int index, T element) {
        if (sectionStarted)
            throw new UnsupportedOperationException("Section started so use add(T) instead");
        if (count == 0 || lastIndex != index) {
            content.add(index);
            lastIndex = index;
        }
        content.add(element);
        count++;
    }
    
    /** can only be used if section has been started, otherwise use section specific version <code>add(int index, T element);</code> */
    public void add(T element) {
        if (!sectionStarted)
            throw new UnsupportedOperationException("No section started so use add(int, T) instead or start a section first");
        content.add(element);
        count++;
    }
    
    public void startSection(int index) {
        if (count == 0 || lastIndex != index) {
            content.add(index);
            lastIndex = index;
        }
        sectionStarted = true;
    }
    
    public void endSection() {
        while (!content.isEmpty() && content.get(content.size() - 1).getClass() == Integer.class)
            content.remove(content.size() - 1);
        sectionStarted = false;
    }
    
    public boolean isEmpty() {
        return count == 0;
    }
    
    @Override
    public Iterator<T> iterator() {
        if (sectionStarted)
            throw new UnsupportedOperationException("Cannot iterate while section has not ended");
        return new FilterIterator<>(content.iterator(), PREDICATE);
    }
    
    public Iterator<T> sectionIterator(IntConsumer consumer) {
        if (sectionStarted)
            throw new UnsupportedOperationException("Cannot iterate while section has not ended");
        return new ComputeNextIterator<T>() {
            
            Iterator<T> itr = content.iterator();
            
            @Override
            protected T computeNext() {
                if (!itr.hasNext())
                    return end();
                
                T next = itr.next();
                if (next.getClass() == Integer.class) {
                    consumer.accept((Integer) next);
                    next = itr.next();
                }
                return next;
            }
        };
    }
    
}
