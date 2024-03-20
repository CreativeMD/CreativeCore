package team.creative.creativecore.common.util.type.map;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class HashMapFloat<K> extends HashMap<K, Float> {
    
    public HashMapFloat() {
        super();
    }
    
    public HashMapFloat(Map<? extends K, ? extends Float> paramMap) {
        super(paramMap);
    }
    
    public void scale(float scale) {
        for (Entry<K, Float> entry : entrySet())
            entry.setValue(entry.getValue() * scale);
    }
    
    @Override
    public Float put(K paramK, Float paramV) {
        Float value = get(paramK);
        if (value != null)
            value += paramV;
        else
            value = paramV;
        return super.put(paramK, value);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends Float> paramMap) {
        for (Entry<? extends K, ? extends Float> entry : paramMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (Entry<K, Float> entry : entrySet()) {
            if (first)
                first = false;
            else
                builder.append(",");
            builder.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        builder.append("]");
        return builder.toString();
    }
    
    public String toString(DecimalFormat df) {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (Entry<K, Float> entry : entrySet()) {
            if (first)
                first = false;
            else
                builder.append(",");
            builder.append(entry.getKey()).append(": ").append(df.format(entry.getValue()));
        }
        builder.append("]");
        return builder.toString();
    }
}
