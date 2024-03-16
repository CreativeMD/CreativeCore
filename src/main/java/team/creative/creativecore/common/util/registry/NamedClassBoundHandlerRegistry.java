package team.creative.creativecore.common.util.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class NamedClassBoundHandlerRegistry<C, T> {
    
    public static void clearRegistry(NamedClassBoundHandlerRegistry registry) {
        registry.handlers.clear();
        registry.handlersInv.clear();
        registry.defaultHandler = null;
    }
    
    private final HashMap<String, T> handlers = new LinkedHashMap<>();
    private final HashMap<T, String> handlersInv = new LinkedHashMap<>();
    private final HashMap<Class<? extends C>, T> classHandlers = new LinkedHashMap<>();
    private T defaultHandler;
    private boolean allowOverwrite = false;
    
    public NamedClassBoundHandlerRegistry() {}
    
    public NamedClassBoundHandlerRegistry<C, T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    public T getDefault() {
        return defaultHandler;
    }
    
    public void register(String id, Class clazz, T handler) {
        if (!allowOverwrite && handlers.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        handlers.put(id, handler);
        handlersInv.put(handler, id);
        classHandlers.put(clazz, handler);
    }
    
    public void registerDefault(String id, Class<? extends C> clazz, T handler) {
        defaultHandler = handler;
        register(id, clazz, handler);
    }
    
    public String getId(T type) {
        return handlersInv.get(type);
    }
    
    public String getId(Class<? extends C> clazz) {
        return handlersInv.get(get(clazz));
    }
    
    public T get(String id) {
        return handlers.getOrDefault(id, defaultHandler);
    }
    
    public T get(Class<? extends C> clazz) {
        return classHandlers.getOrDefault(clazz, defaultHandler);
    }
    
    public T getOrThrow(String id) {
        T handler = handlers.get(id);
        if (handler == null)
            throw new IllegalArgumentException("'" + id + "' does not exist");
        return handler;
    }
    
    public T getOrThrow(Class<? extends C> clazz) {
        T handler = classHandlers.get(clazz);
        if (handler == null)
            throw new IllegalArgumentException("'" + clazz.getName() + "' does not exist");
        return handler;
    }
    
    public boolean contains(String id) {
        return handlers.containsKey(id);
    }
    
    public boolean contains(Class clazz) {
        return classHandlers.containsKey(clazz);
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
    
    public Set<Class<? extends C>> classes() {
        return classHandlers.keySet();
    }
}
