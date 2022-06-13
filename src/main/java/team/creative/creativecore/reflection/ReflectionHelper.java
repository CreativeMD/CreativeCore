package team.creative.creativecore.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper.UnableToFindMethodException;

public class ReflectionHelper {
    
    public static <T> Field findField(Class<? super T> clazz, String offical, String obfuscated) {
        try {
            Field f = clazz.getDeclaredField(obfuscated);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            try {
                Field f = clazz.getDeclaredField(offical);
                f.setAccessible(true);
                return f;
            } catch (Exception e2) {
                throw new RuntimeException("Unable to locate field " + clazz.getSimpleName() + "." + offical + " (" + obfuscated + ")", e2);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + clazz.getSimpleName() + "." + offical + " (" + obfuscated + ")", e);
        }
    }
    
    public static <T> Field findField(Class<? super T> clazz, String offical) {
        try {
            Field f = clazz.getDeclaredField(offical);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + clazz.getSimpleName() + "." + offical, e);
        }
    }
    
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (Exception e) {
            throw new UnableToFindMethodException(e);
        }
    }
}
