package team.creative.creativecore.common.config.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ConfigFieldDynamic implements ConfigField {
    
    private Object value;
    
    public ConfigFieldDynamic(Object value) {
        this.value = value;
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
        return value.getClass();
    }
    
    @Override
    public Class getType() {
        return value.getClass();
    }
    
}
