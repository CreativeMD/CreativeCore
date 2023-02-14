package team.creative.creativecore.common.util.unsafe;

public class CreativeHackery {
    
    private static final sun.misc.Unsafe UNSAFE;
    
    static {
        try {
            UNSAFE = getUnsafe();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    private static sun.misc.Unsafe getUnsafe() {
        try {
            return sun.misc.Unsafe.getUnsafe();
        } catch (SecurityException tryReflectionInstead) {}
        try {
            return java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<sun.misc.Unsafe>() {
                @Override
                public sun.misc.Unsafe run() throws Exception {
                    Class<sun.misc.Unsafe> k = sun.misc.Unsafe.class;
                    for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                        f.setAccessible(true);
                        Object x = f.get(null);
                        if (k.isInstance(x)) {
                            return k.cast(x);
                        }
                    }
                    throw new NoSuchFieldError("the Unsafe");
                }
            });
        } catch (java.security.PrivilegedActionException e) {
            throw new RuntimeException("Could not initialize intrinsics", e.getCause());
        }
    }
    
    public static <T> T allocateInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new Error(e);
        }
    }
    
}
