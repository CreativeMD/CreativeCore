package team.creative.creativecore.common.util.math.base;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.geo.NormalPlane;

public enum Facing {
    
    DOWN(Axis.Y, false, new Vec3i(0, -1, 0)) {
        @Override
        public Facing opposite() {
            return Facing.UP;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.DOWN;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.minY;
        }
    },
    UP(Axis.Y, true, new Vec3i(0, 1, 0)) {
        @Override
        public Facing opposite() {
            return Facing.DOWN;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.UP;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.maxY;
        }
    },
    NORTH(Axis.Z, false, new Vec3i(0, 0, -1)) {
        @Override
        public Facing opposite() {
            return SOUTH;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.NORTH;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.minZ;
        }
    },
    SOUTH(Axis.Z, true, new Vec3i(0, 0, 1)) {
        @Override
        public Facing opposite() {
            return Facing.NORTH;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.SOUTH;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.maxZ;
        }
    },
    WEST(Axis.X, false, new Vec3i(-1, 0, 0)) {
        @Override
        public Facing opposite() {
            return Facing.EAST;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.WEST;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.minX;
        }
    },
    EAST(Axis.X, true, new Vec3i(1, 0, 0)) {
        @Override
        public Facing opposite() {
            return Facing.WEST;
        }
        
        @Override
        public Direction toVanilla() {
            return Direction.EAST;
        }
        
        @Override
        public double get(AABB bb) {
            return bb.maxX;
        }
    };
    
    public static final String[] FACING_NAMES = new String[] { "down", "up", "north", "south", "west", "east" };
    public static final String[] HORIZONTAL_FACING_NAMES = new String[] { "north", "south", "west", "east" };
    
    public static Facing get(int index) {
        switch (index) {
        case 0:
            return Facing.DOWN;
        case 1:
            return Facing.UP;
        case 2:
            return Facing.NORTH;
        case 3:
            return Facing.SOUTH;
        case 4:
            return Facing.WEST;
        case 5:
            return Facing.EAST;
        }
        throw new IllegalArgumentException();
    }
    
    public static Facing get(Direction direction) {
        switch (direction) {
        case DOWN:
            return Facing.DOWN;
        case UP:
            return Facing.UP;
        case NORTH:
            return Facing.NORTH;
        case SOUTH:
            return Facing.SOUTH;
        case WEST:
            return Facing.WEST;
        case EAST:
            return Facing.EAST;
        }
        throw new IllegalArgumentException();
    }
    
    public static Facing get(Axis axis, boolean positive) {
        switch (axis) {
        case X:
            return positive ? Facing.EAST : Facing.WEST;
        case Y:
            return positive ? Facing.UP : Facing.DOWN;
        case Z:
            return positive ? Facing.SOUTH : Facing.NORTH;
        }
        throw new IllegalArgumentException();
    }
    
    public final String name;
    public final Axis axis;
    public final boolean positive;
    public final Vec3i normal;
    public final NormalPlane plane;
    
    private Facing(Axis axis, boolean positive, Vec3i normal) {
        this.name = name().toLowerCase();
        this.axis = axis;
        this.positive = positive;
        this.normal = normal;
        this.plane = new NormalPlane(this);
    }
    
    public int offset() {
        return positive ? 1 : -1;
    }
    
    public abstract Facing opposite();
    
    public abstract Direction toVanilla();
    
    public Axis one() {
        return axis.one();
    }
    
    public Axis two() {
        return axis.two();
    }
    
    public Axis getUAxis() {
        switch (axis) {
        case X:
            return Axis.Z;
        case Y:
            return Axis.X;
        case Z:
            return Axis.X;
        }
        return null;
    }
    
    public Axis getVAxis() {
        switch (axis) {
        case X:
            return Axis.Y;
        case Y:
            return Axis.Z;
        case Z:
            return Axis.Y;
        }
        return null;
    }
    
    public float getU(float x, float y, float z) {
        switch (axis) {
        case X:
            return z;
        case Y:
            return x;
        case Z:
            return x;
        }
        return 0;
    }
    
    public float getV(float x, float y, float z) {
        switch (axis) {
        case X:
            return y;
        case Y:
            return z;
        case Z:
            return y;
        }
        return 0;
    }
    
    public abstract double get(AABB bb);
    
}
