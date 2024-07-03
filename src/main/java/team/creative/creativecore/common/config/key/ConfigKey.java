package team.creative.creativecore.common.config.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.Side;

public abstract class ConfigKey {
    
    public abstract <T extends Annotation> T getAnnotation(Class<T> annotationClass);
    
    public abstract Type getGenericType();
    
    public abstract Class getType();
    
    public abstract Object get();
    
    public abstract void read(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side);
    
    public abstract JsonElement write(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side);
    
    public abstract Object copy(HolderLookup.Provider provider, Side side);
    
}
