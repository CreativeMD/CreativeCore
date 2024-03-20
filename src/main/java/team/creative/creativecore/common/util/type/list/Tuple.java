package team.creative.creativecore.common.util.type.list;

import java.util.Map.Entry;

public class Tuple<K, V> implements Entry<K, V> {
    
    public K key;
    public V value;
    
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public K getKey() {
        return key;
    }
    
    @Override
    public V getValue() {
        return value;
    }
    
    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    public boolean is(K key) {
        if (this.key != null)
            return this.key.equals(key);
        return key == null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple tuple)
            return key.equals(tuple.key);
        return false;
    }
    
    @Override
    public String toString() {
        return "(" + key + "," + value + ")";
    }
}
