package team.creative.creativecore.common.util.registry;

public interface ICreativeRegistry<T> {
    
    public T get(String id);
    
    public String name(T value);
    
    public Iterable<String> names();
    
    public Iterable<T> values();
    
    public default String next(String id) {
        String first = null;
        boolean found = false;
        for (String key : names()) {
            if (found)
                return key;
            if (first == null)
                first = key;
            if (key.equals(id))
                found = true;
        }
        return first;
    }
    
}
