package team.creative.creativecore.common.util.type.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class TupleList<K, V> extends ArrayList<Tuple<K, V>> {
    
    protected Iterable<K> keys = new Iterable<>() {
        
        @Override
        public Iterator<K> iterator() {
            return new FunctionIterator<K>(TupleList.this, x -> x.key);
        }
        
    };
    protected Iterable<V> values = new Iterable<>() {
        
        @Override
        public Iterator<V> iterator() {
            return new FunctionIterator<V>(TupleList.this, x -> x.value);
        }
        
    };
    
    public TupleList() {
        super();
    }
    
    public TupleList(List<Tuple<K, V>> list) {
        super(list);
    }
    
    public boolean add(K key, V value) {
        return add(new Tuple<>(key, value));
    }
    
    public boolean containsKey(K key) {
        return indexOfKey(key) != -1;
    }
    
    public int indexOfKey(K key) {
        for (int i = 0; i < size(); i++)
            if (get(i).key.equals(key))
                return i;
        return -1;
    }
    
    public Iterable<V> values() {
        return values;
    }
    
    public Iterable<K> keys() {
        return keys;
    }
    
    public Tuple<K, V> getFirst() {
        if (isEmpty())
            return null;
        return get(0);
    }
    
    public Tuple<K, V> getLast() {
        if (isEmpty())
            return null;
        return get(size() - 1);
    }
    
    @Nullable
    public V findValue(K key) {
        Tuple<K, V> tuple = findTuple(key);
        if (tuple != null)
            return tuple.value;
        return null;
    }
    
    @Nullable
    public Tuple<K, V> findTuple(K key) {
        int index = indexOfKey(key);
        if (index != -1)
            return get(index);
        return null;
    }
}
