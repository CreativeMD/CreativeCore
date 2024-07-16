package team.creative.creativecore.common.config.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ConfigFieldTyped implements ConfigField {
    
    private Object value;
    private final Type type;
    private final Class clazz;
    
    public ConfigFieldTyped(Object value, Type type, Class clazz) {
        this.value = value;
        this.type = type;
        this.clazz = clazz;
    }
    
    @Override
    public Object get() {
        return value;
    }
    
    @Override
    public void set(Object value) {
        this.value = value;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }
    
    @Override
    public Type getGenericType() {
        return type;
    }
    
    @Override
    public Class getType() {
        return clazz;
    }
    
}
