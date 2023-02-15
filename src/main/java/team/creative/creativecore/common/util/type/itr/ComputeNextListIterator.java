package team.creative.creativecore.common.util.type.itr;

import java.util.ListIterator;

public abstract class ComputeNextListIterator<T> implements ListIterator<T> {
    
    private T next;
    private boolean searchedNext;
    private boolean endNext;
    
    private T previous;
    private boolean searchedPrevious;
    private boolean endPrevious;
    
    public ComputeNextListIterator() {}
    
    protected abstract T computeNext();
    
    protected T endNext() {
        endNext = true;
        return null;
    }
    
    @Override
    public boolean hasNext() {
        if (!searchedNext) {
            next = computeNext();
            searchedNext = true;
        }
        return !endNext;
    }
    
    @Override
    public T next() {
        searchedNext = searchedPrevious = false;
        return next;
    }
    
    protected abstract T computePrevious();
    
    protected T endPrevious() {
        endPrevious = true;
        return null;
    }
    
    @Override
    public boolean hasPrevious() {
        if (!searchedPrevious) {
            previous = computePrevious();
            searchedPrevious = true;
        }
        return !endPrevious;
    }
    
    @Override
    public T previous() {
        searchedNext = searchedPrevious = false;
        return previous;
    }
    
}
