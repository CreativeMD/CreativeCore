package team.creative.creativecore.common.util.filter;

@FunctionalInterface
public interface BiFilter<T, U> {
    
    public static final BiFilterSerializer SERIALIZER = new BiFilterSerializer();
    
    public boolean is(T t, U u);
    
    public static <T, U> BiFilter<T, U> and(BiFilter<T, U>... filters) {
        return new BiFilterAnd<>(filters);
    }
    
    public static <T, U> BiFilter<T, U> or(BiFilter<T, U>... filters) {
        return new BiFilterOr<>(filters);
    }
    
    public static <T, U> BiFilter<T, U> not(BiFilter<T, U> filter) {
        return new BiFilterNot<>(filter);
    }

    public record BiFilterNot<T, U>(BiFilter<T, U> filter) implements BiFilter<T, U> {

        @Override
            public boolean is(T t, U u) {
                return !filter.is(t, u);
            }

    }

    public record BiFilterAnd<T, U>(BiFilter<T, U>... filters) implements BiFilter<T, U> {

        @Override
            public boolean is(T t, U u) {
                for (int i = 0; i < filters.length; i++)
                    if (!filters[i].is(t, u))
                        return false;
                return true;
            }

    }

    public record BiFilterOr<T, U>(BiFilter<T, U>... filters) implements BiFilter<T, U> {

        @Override
            public boolean is(T t, U u) {
                for (int i = 0; i < filters.length; i++)
                    if (filters[i].is(t, u))
                        return true;
                return false;
            }

    }
    
}
