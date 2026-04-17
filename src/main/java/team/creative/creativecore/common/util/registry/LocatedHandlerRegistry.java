package team.creative.creativecore.common.util.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.resources.Identifier;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class LocatedHandlerRegistry<T> implements ICreativeRegistry<T> {
    
    private final HashMap<Identifier, T> handlers = new LinkedHashMap<>();
    private final HashMap<T, Identifier> handlersInv = new LinkedHashMap<>();
    private T defaultHandler;
    private boolean allowOverwrite = false;
    
    public LocatedHandlerRegistry(T handler) {
        this.defaultHandler = handler;
    }
    
    public LocatedHandlerRegistry<T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    public T getDefault() {
        return defaultHandler;
    }
    
    public void register(Identifier id, T handler) {
        if (!allowOverwrite && handlers.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        handlers.put(id, handler);
        handlersInv.put(handler, id);
    }
    
    public void registerDefault(Identifier id, T handler) {
        defaultHandler = handler;
        register(id, handler);
    }
    
    public Identifier getLocation(T type) {
        return handlersInv.get(type);
    }
    
    public T get(Identifier id) {
        return handlers.getOrDefault(id, defaultHandler);
    }
    
    public T getOrThrow(Identifier id) {
        T handler = handlers.get(id);
        if (handler == null)
            throw new IllegalArgumentException("'" + id + "' does not exist");
        return handler;
    }
    
    public boolean contains(Identifier id) {
        return handlers.containsKey(id);
    }
    
    public Collection<Identifier> keys() {
        return handlers.keySet();
    }
    
    public Set<Entry<Identifier, T>> entrySet() {
        return handlers.entrySet();
    }
    
    @Override
    public Collection<T> values() {
        return handlers.values();
    }
    
    @Override
    public T get(String id) {
        return get(Identifier.parse(id));
    }
    
    @Override
    public String name(T value) {
        return getLocation(value).toString();
    }
    
    @Override
    public Iterable<String> names() {
        return new FunctionIterator<>(keys(), x -> x.toString());
    }
}
