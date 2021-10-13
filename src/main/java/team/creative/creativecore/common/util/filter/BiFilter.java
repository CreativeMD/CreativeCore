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
    
    public static class BiFilterNot<T, U> implements BiFilter<T, U> {
        
        public final BiFilter<T, U> filter;
        
        public BiFilterNot(BiFilter<T, U> filter) {
            this.filter = filter;
        }
        
        @Override
        public boolean is(T t, U u) {
            return !filter.is(t, u);
        }
        
    }
    
    public static class BiFilterAnd<T, U> implements BiFilter<T, U> {
        
        public final BiFilter<T, U>[] filters;
        
        public BiFilterAnd(BiFilter<T, U>... filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean is(T t, U u) {
            for (int i = 0; i < filters.length; i++)
                if (!filters[i].is(t, u))
                    return false;
            return true;
        }
        
    }
    
    public static class BiFilterOr<T, U> implements BiFilter<T, U> {
        
        public final BiFilter<T, U>[] filters;
        
        public BiFilterOr(BiFilter<T, U>... filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean is(T t, U u) {
            for (int i = 0; i < filters.length; i++)
                if (filters[i].is(t, u))
                    return true;
            return false;
        }
        
    }
    
}
