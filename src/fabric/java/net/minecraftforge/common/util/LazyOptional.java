package net.minecraftforge.common.util;

import java.util.function.Supplier;

public class LazyOptional<T> {
    
    public static final LazyOptional EMPTY = new LazyOptional<>();
    
    public boolean isPresent() {
        return false;
    }
    
    public <X> LazyOptional<X> cast() {
        return (LazyOptional<X>) this;
    }
    
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        throw exceptionSupplier.get();
    }
    
}
