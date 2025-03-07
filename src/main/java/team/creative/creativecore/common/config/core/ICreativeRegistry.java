package team.creative.creativecore.common.config.core;

import java.lang.reflect.Field;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;

public interface ICreativeRegistry {
    
    public ConfigEqualChecker getEqualChecker();
    
    public boolean is(Field field);
    
    public default boolean is(Field field, Side side) {
        if (!is(field))
            return false;
        CreativeConfig config = field.getAnnotation(CreativeConfig.class);
        return config == null || config.type().useValue(false, side);
    }
    
    public default boolean equals(Object one, Object two, Side side) {
        return getEqualChecker().equals(one, two, side, this);
    }
    
}
