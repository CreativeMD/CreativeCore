package team.creative.creativecore.common.util.type.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;

import team.creative.creativecore.common.util.type.itr.InverseConsecutiveIterator;
import team.creative.creativecore.common.util.type.itr.InverseListIterator;
import team.creative.creativecore.common.util.type.itr.NestedIterator;

public class BucketList<T> implements Iterable<T> {
    
    private final List<List<T>> buckets = new ArrayList<>();
    private int size = 0;
    
    public BucketList() {}
    
    public BucketList(int initialBucketSize) {
        ensureBucketSize(initialBucketSize);
    }
    
    public BucketList(Iterable<T> content, ToIntFunction<T> bucketSorter) {
        addAll(content, bucketSorter);
    }
    
    private void ensureBucketSize(int size) {
        for (int i = buckets.size(); i < size; i++)
            buckets.add(new ArrayList<T>());
    }
    
    private void recountSize() {
        size = 0;
        for (List<T> bucket : buckets)
            size += bucket.size();
    }
    
    protected List<T> getBucket(int bucket, boolean create) {
        if (create) {
            ensureBucketSize(bucket);
            return buckets.get(bucket);
        }
        
        if (buckets.size() > bucket)
            throw new IllegalArgumentException("Bucket index '" + bucket + "' is out of bounds (total " + buckets.size() + ")");
        return buckets.get(bucket);
    }
    
    public void add(int bucket, T element) {
        ensureBucketSize(bucket);
        getBucket(bucket, true).add(element);
        size++;
    }
    
    public void addAll(Iterable<T> content, ToIntFunction<T> bucketSorter) {
        for (T element : content)
            add(bucketSorter.applyAsInt(element), element);
    }
    
    public void remove(int bucket, T element) {
        if (buckets.size() > bucket && getBucket(bucket, false).remove(element))
            size--;
    }
    
    public void removeAll(int bucket, Collection<T> elements) {
        if (buckets.size() > bucket && getBucket(bucket, false).removeAll(elements))
            recountSize();
    }
    
    public List<T> removeBucket(int bucket) {
        return buckets.remove(bucket);
    }
    
    public void clear() {
        for (List<T> bucket : buckets)
            bucket.clear();
    }
    
    public void removeBuckets() {
        buckets.clear();
    }
    
    public Iterable<? extends Iterable<T>> buckets() {
        return buckets;
    }
    
    public int bucketCount() {
        return buckets.size();
    }
    
    public int size() {
        return size;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new NestedIterator<>(buckets);
    }
    
    public Iterator<T> inverseIterator() {
        Iterator[] itrs = new Iterator[buckets.size()];
        for (int i = 0; i < itrs.length; i++)
            itrs[i] = new InverseListIterator<>(buckets.get(i));
        return new InverseConsecutiveIterator<>(itrs);
    }
    
}
