package team.creative.creativecore.common.util.math.base;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;

public enum Axis {
    
    X {
        @Override
        public double get(double x, double y, double z) {
            return x;
        }
        
        @Override
        public float get(float x, float y, float z) {
            return x;
        }
        
        @Override
        public int get(int x, int y, int z) {
            return x;
        }
        
        @Override
        public <T> T get(T x, T y, T z) {
            return x;
        }
        
        @Override
        public Axis one() {
            return Axis.Y;
        }
        
        @Override
        public Axis two() {
            return Axis.Z;
        }
        
        @Override
        public Facing facing(boolean positive) {
            return positive ? Facing.EAST : Facing.WEST;
        }
        
        @Override
        public Direction.Axis toVanilla() {
            return Direction.Axis.X;
        }
    },
    Y {
        @Override
        public double get(double x, double y, double z) {
            return y;
        }
        
        @Override
        public float get(float x, float y, float z) {
            return y;
        }
        
        @Override
        public int get(int x, int y, int z) {
            return y;
        }
        
        @Override
        public <T> T get(T x, T y, T z) {
            return y;
        }
        
        @Override
        public Axis one() {
            return Axis.Z;
        }
        
        @Override
        public Axis two() {
            return Axis.X;
        }
        
        @Override
        public Facing facing(boolean positive) {
            return positive ? Facing.UP : Facing.DOWN;
        }
        
        @Override
        public Direction.Axis toVanilla() {
            return Direction.Axis.Y;
        }
    },
    Z {
        @Override
        public double get(double x, double y, double z) {
            return z;
        }
        
        @Override
        public float get(float x, float y, float z) {
            return z;
        }
        
        @Override
        public int get(int x, int y, int z) {
            return z;
        }
        
        @Override
        public <T> T get(T x, T y, T z) {
            return z;
        }
        
        @Override
        public Axis one() {
            return Axis.X;
        }
        
        @Override
        public Axis two() {
            return Axis.Y;
        }
        
        @Override
        public Facing facing(boolean positive) {
            return positive ? Facing.SOUTH : Facing.NORTH;
        }
        
        @Override
        public Direction.Axis toVanilla() {
            return Direction.Axis.Z;
        }
    };
    
    public static Axis get(Direction.Axis axis) {
        switch (axis) {
        case X:
            return Axis.X;
        case Y:
            return Axis.Y;
        case Z:
            return Axis.Z;
        }
        throw new IllegalArgumentException();
    }
    
    public static Axis third(Axis one, Axis two) {
        switch (one) {
        case X:
            if (two == Axis.Y)
                return Axis.Z;
            return Axis.Y;
        case Y:
            if (two == Axis.X)
                return Axis.Z;
            return Axis.X;
        case Z:
            if (two == Axis.Y)
                return Axis.X;
            return Axis.Y;
        }
        throw new IllegalArgumentException();
    }
    
    public static Axis getMirrorAxis(Mirror mirrorIn) {
        switch (mirrorIn) {
        case FRONT_BACK:
            return Axis.X;
        case LEFT_RIGHT:
            return Axis.Z;
        default:
            return null;
        }
    }
    
    public abstract Axis one();
    
    public abstract Axis two();
    
    public abstract Facing facing(boolean positive);
    
    public abstract double get(double x, double y, double z);
    
    public abstract float get(float x, float y, float z);
    
    public abstract int get(int x, int y, int z);
    
    public abstract <T> T get(T x, T y, T z);
    
    public abstract Direction.Axis toVanilla();
    
}
