package team.creative.creativecore.common.util.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class NamedHandlerRegistry<T> {
    
    public static void clearRegistry(NamedHandlerRegistry registry) {
        registry.handlers.clear();
        registry.handlersInv.clear();
        registry.defaultHandler = null;
    }
    
    private final HashMap<String, T> handlers = new LinkedHashMap<>();
    private final HashMap<T, String> handlersInv = new LinkedHashMap<>();
    private T defaultHandler;
    private boolean allowOverwrite = false;
    
    public NamedHandlerRegistry(T handler) {
        this.defaultHandler = handler;
    }
    
    public NamedHandlerRegistry<T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    public T getDefault() {
        return defaultHandler;
    }
    
    public void register(String id, T handler) {
        if (!allowOverwrite && handlers.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        handlers.put(id, handler);
        handlersInv.put(handler, id);
    }
    
    public void registerDefault(String id, T handler) {
        defaultHandler = handler;
        register(id, handler);
    }
    
    public String getId(T type) {
        return handlersInv.get(type);
    }
    
    public T get(String id) {
        return handlers.getOrDefault(id, defaultHandler);
    }
    
    public T getOrThrow(String id) {
        T handler = handlers.get(id);
        if (handler == null)
            throw new IllegalArgumentException("'" + id + "' does not exist");
        return handler;
    }
    
    public boolean contains(String id) {
        return handlers.containsKey(id);
    }
    
    public Collection<String> keys() {
        return handlers.keySet();
    }
    
    public Set<Entry<String, T>> entrySet() {
        return handlers.entrySet();
    }
    
    public Collection<T> values() {
        return handlers.values();
    }
}
