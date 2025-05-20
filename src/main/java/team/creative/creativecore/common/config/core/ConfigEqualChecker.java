package team.creative.creativecore.common.config.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.util.type.TriPredicate;

public class ConfigEqualChecker {
    
    private final HashMap<Class, TriPredicate<Object, Object, Side>> checkers = new HashMap<>();
    private final HashSet<Class> toIgnore = new HashSet<>();
    
    /** Will be called automatically when calling registerTypeCreator */
    public TriPredicate<Object, Object, Side> register(Class clazz, ICreativeRegistry registry) {
        try {
            var method = clazz.getDeclaredMethod("equals", Object.class);
            if (method != null) {
                toIgnore.add(clazz);
                return null;
            }
        } catch (NoSuchMethodException | SecurityException e) {}
        
        List<Field> fields = new ArrayList<>();
        ConfigHolderObject.collectFields(clazz, fields, registry);
        TriPredicate<Object, Object, Side> result = (x, y, side) -> {
            
            if (fields.isEmpty())
                return true;
            
            for (int i = 0; i < fields.size(); i++) {
                var field = fields.get(i);
                if (!registry.is(field, side))
                    continue;
                
                try {
                    if (!equals(field.get(x), field.get(y), side, registry))
                        return false;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    CreativeCore.LOGGER.error("Could not check equals on object with class " + clazz, e);
                    throw new RuntimeException(e);
                }
            }
            
            return true;
        };
        checkers.put(clazz, result);
        return result;
    }
    
    public boolean equals(Object one, Object two, Side side, ICreativeRegistry registry) {
        if (Objects.equals(one, two))
            return true;
        
        if (one == null || two == null)
            return false;
        
        if (one.getClass() != two.getClass())
            return false;
        
        if (toIgnore.contains(one.getClass()))
            return false;
        
        var check = checkers.get(one.getClass());
        if (check == null)
            check = register(one.getClass(), registry);
        if (check != null)
            return check.test(one, two, side);
        return false;
    }
    
}
