package team.creative.creativecore.common.util.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public class LocatedHandlerRegistry<T> {
    
    private HashMap<ResourceLocation, T> handlers = new LinkedHashMap<>();
    private HashMap<T, ResourceLocation> handlersInv = new LinkedHashMap<>();
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
    
    public void register(ResourceLocation id, T handler) {
        if (!allowOverwrite && handlers.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        handlers.put(id, handler);
        handlersInv.put(handler, id);
    }
    
    public void registerDefault(ResourceLocation id, T handler) {
        defaultHandler = handler;
        register(id, handler);
    }
    
    public ResourceLocation getLocation(T type) {
        return handlersInv.get(type);
    }
    
    public T get(ResourceLocation id) {
        return handlers.getOrDefault(id, defaultHandler);
    }
    
    public T getOrThrow(ResourceLocation id) {
        T handler = handlers.get(id);
        if (handler == null)
            throw new IllegalArgumentException("'" + id + "' does not exist");
        return handler;
    }
    
    public boolean contains(ResourceLocation id) {
        return handlers.containsKey(id);
    }
    
    public Collection<ResourceLocation> keys() {
        return handlers.keySet();
    }
    
    public Set<Entry<ResourceLocation, T>> entrySet() {
        return handlers.entrySet();
    }
    
    public Collection<T> values() {
        return handlers.values();
    }
}
