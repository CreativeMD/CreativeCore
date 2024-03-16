package team.creative.creativecore.common.util.filter;

@FunctionalInterface
public interface BiFilter<T, U> {
    BiFilterSerializer SERIALIZER = new BiFilterSerializer();

    boolean is(T t, U u);

    @SafeVarargs
    static <T, U> BiFilter<T, U> and(BiFilter<T, U>... filters) {
        return new BiFilterAnd<>(filters);
    }

    @SafeVarargs
    static <T, U> BiFilter<T, U> or(BiFilter<T, U>... filters) {
        return new BiFilterOr<>(filters);
    }

    static <T, U> BiFilter<T, U> not(BiFilter<T, U> filter) {
        return new BiFilterNot<>(filter);
    }

    record BiFilterNot<T, U>(BiFilter<T, U> filter) implements BiFilter<T, U> {
        @Override
        public boolean is(T t, U u) {
            return !filter.is(t, u);
        }
    }

    record BiFilterAnd<T, U>(BiFilter<T, U>... filters) implements BiFilter<T, U> {
        @SafeVarargs
        public BiFilterAnd {}

        @Override
        public boolean is(T t, U u) {
            for (BiFilter<T, U> filter : filters)
                if (!filter.is(t, u))
                    return false;
            return true;
        }
    }

    record BiFilterOr<T, U>(BiFilter<T, U>... filters) implements BiFilter<T, U> {
        @SafeVarargs
        public BiFilterOr {}

        @Override
        public boolean is(T t, U u) {
            for (BiFilter<T, U> filter : filters)
                if (filter.is(t, u))
                    return true;
            return false;
        }
    }
}
