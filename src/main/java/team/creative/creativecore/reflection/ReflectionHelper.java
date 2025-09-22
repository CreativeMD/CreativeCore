package team.creative.creativecore.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionHelper {
    
    public static <T> Field findFieldByType(Class<? super T> clazz, String fieldType) {
        try {
            return findFieldByType(clazz, Class.forName(fieldType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to locate class " + fieldType + " for field search in " + clazz.getSimpleName(), e);
        }
    }
    
    public static <T> Field findFieldByType(Class<? super T> clazz, Class fieldType) {
        try {
            var fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
                if (fields[i].getType() == fieldType) {
                    Field f = fields[i];
                    f.setAccessible(true);
                    return f;
                }
            throw new Exception();
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + clazz.getSimpleName() + " with type " + fieldType.getSimpleName(), e);
        }
    }
    
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
            throw new RuntimeException("Unable to locate method " + clazz.getSimpleName() + "." + methodName + "(" + Arrays.toString(parameterTypes) + ")", e);
        }
    }
}
