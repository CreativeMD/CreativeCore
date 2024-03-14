package team.creative.creativecore.common.util.type.map;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class HashMapDouble<K> extends HashMap<K, Double> {
    
    public HashMapDouble() {
        super();
    }
    
    public HashMapDouble(Map<? extends K, ? extends Double> paramMap) {
        super(paramMap);
    }
    
    public void scale(double scale) {
        for (Entry<K, Double> entry : entrySet())
            entry.setValue(entry.getValue() * scale);
    }
    
    @Override
    public Double put(K paramK, Double paramV) {
        Double value = get(paramK);
        if (value != null)
            value += paramV;
        else
            value = paramV;
        return super.put(paramK, value);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends Double> paramMap) {
        for (Entry<? extends K, ? extends Double> entry : paramMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (Entry<K, Double> entry : entrySet()) {
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
        for (Entry<K, Double> entry : entrySet()) {
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
