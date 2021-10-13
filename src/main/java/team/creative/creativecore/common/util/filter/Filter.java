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
    
    public static class FilterNot<T> implements Filter<T> {
        
        public final Filter<T> filter;
        
        public FilterNot(Filter<T> filter) {
            this.filter = filter;
        }
        
        @Override
        public boolean is(T t) {
            return !filter.is(t);
        }
        
    }
    
    public static class FilterAnd<T> implements Filter<T> {
        
        public final Filter<T>[] filters;
        
        public FilterAnd(Filter<T>... filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean is(T t) {
            for (int i = 0; i < filters.length; i++)
                if (!filters[i].is(t))
                    return false;
            return true;
        }
        
    }
    
    public static class FilterOr<T> implements Filter<T> {
        
        public final Filter<T>[] filters;
        
        public FilterOr(Filter<T>... filters) {
            this.filters = filters;
        }
        
        @Override
        public boolean is(T t) {
            for (int i = 0; i < filters.length; i++)
                if (filters[i].is(t))
                    return true;
            return false;
        }
        
    }
    
}
