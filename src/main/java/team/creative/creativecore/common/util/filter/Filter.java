package team.creative.creativecore.common.util.filter;

@FunctionalInterface
public interface Filter<T> {
    
    public static final FilterSerializer SERIALIZER = new FilterSerializer();
    
    public boolean is(T t);
    
    public static <T> Filter<T> and(Filter<T>... filters) {
        return new FilterAnd<>(filters);
    }
    
    public static <T> Filter<T> or(Filter<T>... filters) {
        return new FilterOr<>(filters);
    }
    
    public static <T> Filter<T> not(Filter<T> filter) {
        return new FilterNot<>(filter);
    }

    public record FilterNot<T>(Filter<T> filter) implements Filter<T> {

        @Override
            public boolean is(T t) {
                return !filter.is(t);
            }

    }

    public record FilterAnd<T>(Filter<T>... filters) implements Filter<T> {

        @Override
            public boolean is(T t) {
                for (int i = 0; i < filters.length; i++)
                    if (!filters[i].is(t))
                        return false;
                return true;
            }

    }

    public record FilterOr<T>(Filter<T>... filters) implements Filter<T> {

        @Override
            public boolean is(T t) {
                for (int i = 0; i < filters.length; i++)
                    if (filters[i].is(t))
                        return true;
                return false;
            }

    }
    
}
