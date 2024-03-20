package team.creative.creativecore.common.util.type.itr;

public abstract class ComputeNextIterator<T> implements IterableIterator<T> {
    
    private T next;
    private boolean searched;
    private boolean end;
    
    public ComputeNextIterator() {}
    
    protected abstract T computeNext();
    
    protected T end() {
        end = true;
        return null;
    }
    
    @Override
    public boolean hasNext() {
        if (!searched) {
            next = computeNext();
            searched = true;
        }
        return !end;
    }
    
    @Override
    public T next() {
        searched = false;
        return next;
    }
    
}
