package team.creative.creativecore.common.util.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import team.creative.creativecore.common.util.filter.Filter;
import team.creative.creativecore.common.util.type.Pair;
import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class FilteredHandlerRegistry<U, T> {
    
    private HashMap<U, T> map = new HashMap<>();
    private List<Pair<Filter<U>, T>> handlers = new ArrayList<>();
    private T defaultHandler;
    private boolean allowOverwrite = false;
    
    public FilteredHandlerRegistry(T handler) {
        this.defaultHandler = handler;
    }
    
    public FilteredHandlerRegistry<U, T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    public T getDefault() {
        return defaultHandler;
    }
    
    public void register(U value, T handler) {
        if (!allowOverwrite && map.containsKey(value))
            throw new IllegalArgumentException("'" + value + "' already exists");
        map.put(value, handler);
    }
    
    public void register(Filter<U> filter, T handler) {
        handlers.add(new Pair<>(filter, handler));
    }
    
    public T get(U value) {
        T result = map.get(value);
        if (result != null)
            return result;
        for (Pair<Filter<U>, T> pair : handlers)
            if (pair.key.is(value))
                return pair.getValue();
        return defaultHandler;
    }
    
    public Iterable<T> handlers() {
        return new Iterable<T>() {
            
            @Override
            public Iterator<T> iterator() {
                return new ConsecutiveIterator<T>(map.values().iterator(), new FunctionIterator<T>(handlers, (pair) -> pair.value));
            }
        };
    }
    
}
