package net.minecraftforge.common.util;

public class LazyOptional<T> {
    
    public static final LazyOptional EMPTY = new LazyOptional<>();
    
    public boolean isPresent() {
        return false;
    }
    
    public <X> LazyOptional<X> cast() {
        return (LazyOptional<X>) this;
    }
    
}
