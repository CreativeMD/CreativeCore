package team.creative.creativecore.common.config.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConfigField {
    
    public Object get();
    
    public void set(Object value);
    
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass);
    
    public Type getGenericType();
    
    public Class getType();
    
}
