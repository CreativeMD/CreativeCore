package team.creative.creativecore.common.util.math.base;

import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.geo.NormalPlaneF;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public enum Facing {
    
    DOWN(Axis.Y, false, new Vec3i(0, -1, 0), -1) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.YN;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMinY(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minY;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minY;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minY;
        }
        
    },
    UP(Axis.Y, true, new Vec3i(0, 1, 0), -1) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.YP;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMaxY(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxY;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxY;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxY;
        }
        
    },
    NORTH(Axis.Z, false, new Vec3i(0, 0, -1), 2) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.ZN;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMinZ(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minZ;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minZ;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minZ;
        }
        
    },
    SOUTH(Axis.Z, true, new Vec3i(0, 0, 1), 0) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.ZP;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMaxZ(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxZ;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxZ;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxZ;
        }
        
    },
    WEST(Axis.X, false, new Vec3i(-1, 0, 0), 1) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.XN;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMinX(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return minX;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return minX;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return minX;
        }
        
    },
    EAST(Axis.X, true, new Vec3i(1, 0, 0), 3) {
        
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
        
        @Override
        public Vector3f rotation() {
            return Vector3f.XP;
        }
        
        @Override
        public AABB set(AABB bb, double value) {
            return bb.setMaxX(value);
        }
        
        @Override
        public float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return maxX;
        }
        
        @Override
        public double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            return maxX;
        }
        
        @Override
        public int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            return maxX;
        }
    };
    
    public static final Facing[] VALUES = new Facing[] { DOWN, UP, NORTH, SOUTH, WEST, EAST };
    public static final Facing[] HORIZONTA_VALUES = new Facing[] { SOUTH, WEST, NORTH, EAST };
    
    public static final String[] FACING_NAMES = new String[] { "down", "up", "north", "south", "west", "east" };
    public static final String[] HORIZONTAL_FACING_NAMES = new String[] { "north", "south", "west", "east" };
    
    public static Facing get(int index) {
        return VALUES[index];
    }
    
    public static Facing get(Direction direction) {
        if (direction == null)
            return null;
        return switch (direction) {
            case DOWN -> Facing.DOWN;
            case UP -> Facing.UP;
            case NORTH -> Facing.NORTH;
            case SOUTH -> Facing.SOUTH;
            case WEST -> Facing.WEST;
            case EAST -> Facing.EAST;
        };
    }
    
    public static Facing get(Axis axis, boolean positive) {
        return switch (axis) {
            case X -> positive ? Facing.EAST : Facing.WEST;
            case Y -> positive ? Facing.UP : Facing.DOWN;
            case Z -> positive ? Facing.SOUTH : Facing.NORTH;
            default -> throw new IllegalArgumentException();
        };
    }
    
    public static Facing getHorizontal(int index) {
        return HORIZONTA_VALUES[index];
    }
    
    /** gets the direction from the first position to the second. It assumes the positions are next to each other.
     * 
     * @param pos
     * @param second
     * @return */
    public static Facing direction(Vec3i pos, Vec3i second) {
        if (pos.getX() == second.getX())
            if (pos.getY() == second.getY())
                if (pos.getZ() == second.getZ() + 1)
                    return Facing.SOUTH;
                else
                    return Facing.NORTH;
            else if (pos.getY() == second.getY() + 1)
                return Facing.UP;
            else
                return Facing.DOWN;
        else if (pos.getX() == second.getX() + 1)
            return Facing.EAST;
        return Facing.WEST;
    }
    
    public static Facing nearest(Vec3f vec) {
        return nearest(vec.x, vec.y, vec.z);
    }
    
    public static Facing nearest(float x, float y, float z) {
        if (x == 0 && y == 0 && z == 0)
            return null;
        
        Facing facing = null;
        float distance = Float.MIN_VALUE;
        
        for (int i = 0; i < VALUES.length; i++) {
            Facing f = VALUES[i];
            float newDistance = x * f.normal.getX() + y * f.normal.getY() + z * f.normal.getZ();
            if (newDistance > distance) {
                distance = newDistance;
                facing = f;
            }
        }
        
        return facing;
    }
    
    public final String name;
    public final Axis axis;
    public final boolean positive;
    public final Vec3i normal;
    public final NormalPlaneF plane;
    public final int horizontalIndex;
    
    private Facing(Axis axis, boolean positive, Vec3i normal, int horizontalIndex) {
        this.name = name().toLowerCase();
        this.axis = axis;
        this.positive = positive;
        this.normal = normal;
        this.plane = new NormalPlaneF(this);
        this.horizontalIndex = horizontalIndex;
    }
    
    public int offset() {
        return positive ? 1 : -1;
    }
    
    public int offset(Axis axis) {
        if (this.axis == axis)
            return offset();
        return 0;
    }
    
    public Component translate() {
        return new TranslatableComponent("facing." + name);
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
        return switch (axis) {
            case X -> Axis.Z;
            case Y -> Axis.X;
            case Z -> Axis.X;
            default -> null;
        };
    }
    
    public Axis getVAxis() {
        return switch (axis) {
            case X -> Axis.Y;
            case Y -> Axis.Z;
            case Z -> Axis.Y;
            default -> null;
        };
    }
    
    public float getU(float x, float y, float z) {
        return switch (axis) {
            case X -> z;
            case Y -> x;
            case Z -> x;
            default -> 0;
        };
    }
    
    public float getV(float x, float y, float z) {
        return switch (axis) {
            case X -> y;
            case Y -> z;
            case Z -> y;
            default -> 0;
        };
    }
    
    public abstract double get(AABB bb);
    
    public abstract AABB set(AABB bb, double value);
    
    public abstract Vector3f rotation();
    
    public abstract float get(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);
    
    public abstract double get(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
    
    public abstract int get(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
    
}
