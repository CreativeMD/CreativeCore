package team.creative.creativecore.common.config.premade;

import java.util.LinkedHashMap;
import java.util.Set;

public class NamedList<T> extends LinkedHashMap<String, T> {
    
    @Override
    public Set<java.util.Map.Entry<String, T>> entrySet() {
        return super.entrySet();
    }
    
}
