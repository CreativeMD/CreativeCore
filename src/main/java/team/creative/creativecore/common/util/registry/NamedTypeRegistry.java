package team.creative.creativecore.common.util.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import team.creative.creativecore.common.util.registry.exception.IdNotFoundException;
import team.creative.creativecore.common.util.registry.exception.RegistryException;

public class NamedTypeRegistry<T> {
    
    private final HashMap<String, Class<? extends T>> types = new LinkedHashMap<>();
    private final HashMap<Class<? extends T>, String> typesInv = new LinkedHashMap<>();
    private final List<Class[]> possibleConstructors = new ArrayList<>();
    private boolean allowOverwrite = false;
    
    public NamedTypeRegistry<T> addConstructorPattern(Class... classes) {
        possibleConstructors.add(classes);
        return this;
    }
    
    public NamedTypeRegistry<T> allowOverwrite() {
        allowOverwrite = true;
        return this;
    }
    
    protected String printConstructors() {
        StringBuilder builder = new StringBuilder("[");
        for (Class[] classes : possibleConstructors) {
            builder.append("(");
            for (int i = 0; i < classes.length; i++) {
                if (i > 0)
                    builder.append(",");
                builder.append(classes[i].getSimpleName());
            }
            builder.append(")");
        }
        builder.append("]");
        return builder.toString();
    }
    
    protected void checkConstructor(Class<? extends T> clazz) {
        if (possibleConstructors.isEmpty())
            possibleConstructors.add(new Class[0]);
        
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            Parameter[] pars = constructor.getParameters();
            for (Class[] classes : possibleConstructors) {
                if (pars.length != classes.length)
                    continue;
                
                for (int i = 0; i < classes.length; i++) {
                    if (!pars[i].getType().isAssignableFrom(classes[i]))
                        break;
                }
                
                return;
            }
        }
        throw new IllegalArgumentException("Invalid class " + clazz + ". Missing constructor " + printConstructors());
    }
    
    protected void checkConstructor(Object... objects) throws RegistryException {
        for (Class[] classes : possibleConstructors) {
            if (objects.length != classes.length)
                continue;
            
            for (int i = 0; i < classes.length; i++) {
                if (!classes[i].isAssignableFrom(objects[i].getClass()))
                    break;
            }
            
            return;
        }
        throw new ConstructorNotFoundException(objects);
    }
    
    public void register(String id, Class<? extends T> type) {
        if (!allowOverwrite && types.containsKey(id))
            throw new IllegalArgumentException("'" + id + "' already exists");
        types.put(id, type);
        typesInv.put(type, id);
    }
    
    public String getId(T type) {
        return typesInv.get(type.getClass());
    }
    
    public String getIdOrDefault(T type, String defaultValue) {
        return typesInv.getOrDefault(type.getClass(), defaultValue);
    }
    
    public String getId(Class<? extends T> type) {
        return typesInv.get(type);
    }
    
    public Class<? extends T> get(String id) {
        return types.get(id);
    }
    
    public T create(String id, Object... objects) throws RegistryException {
        Class<? extends T> clazz = types.get(id);
        if (clazz == null)
            throw new IdNotFoundException(id);
        
        checkConstructor(objects);
        
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < classes.length; i++)
            classes[i] = objects[i].getClass();
        
        try {
            return clazz.getConstructor(classes).newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new ConstructorForbiddenException(classes, clazz);
        }
    }
    
    public T createSafe(Class<? extends T> ifFailed, String id, Object... objects) {
        try {
            return create(id, objects);
        } catch (RegistryException e) {
            Class[] classes = new Class[objects.length];
            for (int i = 0; i < classes.length; i++)
                classes[i] = objects[i].getClass();
            
            try {
                return ifFailed.getConstructor(classes).newInstance(objects);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e2) {
                throw new RuntimeException("Constructor " + NamedTypeRegistry.toString(classes) + " is not reachable", e2);
            }
        }
    }
    
    public Collection<String> keys() {
        return types.keySet();
    }
    
    public Set<Entry<String, Class<? extends T>>> entrySet() {
        return types.entrySet();
    }
    
    public Collection<Class<? extends T>> values() {
        return types.values();
    }
    
    public boolean contains(String id) {
        return types.containsKey(id);
    }
    
    public static class ConstructorNotFoundException extends RegistryException {
        
        public ConstructorNotFoundException(Object[] objects) {
            super("Constructor " + NamedTypeRegistry.toString(objects) + " does not exists");
        }
        
    }
    
    public static class ConstructorForbiddenException extends RegistryException {
        
        public ConstructorForbiddenException(Class[] classes, Class clazz) {
            super("Constructor " + NamedTypeRegistry.toString(classes) + " is not reachable in " + clazz.getSimpleName());
        }
        
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
