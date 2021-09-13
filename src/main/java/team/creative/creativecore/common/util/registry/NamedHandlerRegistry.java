package team.creative.creativecore.common.util.registry;

import java.util.HashMap;

public class NamedHandlerRegistry<T> {
    
    private HashMap<String, T> handlers = new HashMap<>();
    private HashMap<T, String> handlersInv = new HashMap<>();
    private T defaultHandler;
    private boolean allowOverwrite = false;
    
    public NamedHandlerRegistry(T handler) {
        this.defaultHandler = handler;
    }
    
    public NamedHandlerRegistry<T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    public void register(String id, T handler) {
        if (!allowOverwrite && handlers.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        handlers.put(id, handler);
        handlersInv.put(handler, id);
    }
    
    public String getId(T type) {
        return handlersInv.get(type);
    }
    
    public T get(String id) {
        return handlers.getOrDefault(id, defaultHandler);
    }
    
    protected static String toString(Object[] var0) {
        if (var0 == null) {
            return "null";
        } else {
            int var1 = var0.length - 1;
            if (var1 == -1) {
                return "[]";
            } else {
                StringBuilder var2 = new StringBuilder();
                var2.append('[');
                int var3 = 0;
                
                while (true) {
                    var2.append(var0[var3].getClass().getSimpleName());
                    if (var3 == var1) {
                        return var2.append(']').toString();
                    }
                    
                    var2.append(", ");
                    ++var3;
                }
            }
        }
    }
    
    protected static String toString(Class[] var0) {
        if (var0 == null) {
            return "null";
        } else {
            int var1 = var0.length - 1;
            if (var1 == -1) {
                return "[]";
            } else {
                StringBuilder var2 = new StringBuilder();
                var2.append('[');
                int var3 = 0;
                
                while (true) {
                    var2.append(var0[var3].getSimpleName());
                    if (var3 == var1) {
                        return var2.append(']').toString();
                    }
                    
                    var2.append(", ");
                    ++var3;
                }
            }
        }
    }
}
