package team.creative.creativecore.reflection;

import java.lang.reflect.Field;

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
}
