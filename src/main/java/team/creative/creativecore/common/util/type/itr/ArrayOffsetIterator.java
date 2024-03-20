package team.creative.creativecore.common.util.type.itr;

public class ArrayOffsetIterator<T> implements IterableIterator<T> {
    
    public final T[] content;
    private int offset;
    private int count;
    
    public ArrayOffsetIterator(int offset, T... content) {
        this.content = content;
        this.count = 0;
        this.offset = offset;
    }
    
    @Override
    public boolean hasNext() {
        return count < content.length;
    }
    
    @Override
    public T next() {
        T result = content[offset];
        offset++;
        if (offset >= content.length)
            offset = 0;
        count++;
        return result;
    }
    
}
