package team.creative.creativecore.common.util.type;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    
    public boolean test(T t, U u, V v);
    
}
