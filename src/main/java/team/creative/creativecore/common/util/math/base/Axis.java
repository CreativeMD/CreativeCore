package team.creative.creativecore.common.util.math.base;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

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
        public int get(Vec3i vec) {
            return vec.getX();
        }
        
        @Override
        public Vec3i set(Vec3i vec, int value) {
            return new Vec3i(value, vec.getY(), vec.getZ());
        }
        
        @Override
        public BlockPos set(BlockPos pos, int value) {
            return new BlockPos(value, pos.getY(), pos.getZ());
        }
        
        @Override
        public void set(MutableBlockPos pos, int value) {
            pos.setX(value);
        }
        
        @Override
        public SectionPos set(SectionPos pos, int value) {
            return SectionPos.of(value, pos.getY(), pos.getZ());
        }
        
        @Override
        public int get(ChunkPos pos) {
            return pos.x;
        }
        
        @Override
        public ChunkPos set(ChunkPos pos, int value) {
            return new ChunkPos(value, pos.z);
        }
        
        @Override
        public double get(Vec3 vec) {
            return vec.x;
        }
        
        @Override
        public Vec3 set(Vec3 vec, double value) {
            return new Vec3(value, vec.y, vec.z);
        }
        
        @Override
        public double get(Vector3d vec) {
            return vec.x;
        }
        
        @Override
        public void set(Vector3d vec, double value) {
            vec.x = value;
        }
        
        @Override
        public float get(Vector3f vec) {
            return vec.x();
        }
        
        @Override
        public void set(Vector3f vec, float value) {
            vec.setX(value);
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
        
        @Override
        public Vec3i mirror(Vec3i vec) {
            return new Vec3i(-vec.getX(), vec.getY(), vec.getZ());
        }
        
        @Override
        public BlockPos mirror(BlockPos vec) {
            return new BlockPos(-vec.getX(), vec.getY(), vec.getZ());
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
        public int get(Vec3i vec) {
            return vec.getY();
        }
        
        @Override
        public Vec3i set(Vec3i vec, int value) {
            return new Vec3i(vec.getX(), value, vec.getZ());
        }
        
        @Override
        public BlockPos set(BlockPos pos, int value) {
            return new BlockPos(pos.getX(), value, pos.getZ());
        }
        
        @Override
        public void set(MutableBlockPos pos, int value) {
            pos.setY(value);
        }
        
        @Override
        public SectionPos set(SectionPos pos, int value) {
            return SectionPos.of(pos.getX(), value, pos.getZ());
        }
        
        @Override
        public int get(ChunkPos pos) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ChunkPos set(ChunkPos pos, int value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public double get(Vec3 vec) {
            return vec.y;
        }
        
        @Override
        public Vec3 set(Vec3 vec, double value) {
            return new Vec3(vec.x, value, vec.z);
        }
        
        @Override
        public double get(Vector3d vec) {
            return vec.y;
        }
        
        @Override
        public void set(Vector3d vec, double value) {
            vec.y = value;
        }
        
        @Override
        public float get(Vector3f vec) {
            return vec.y();
        }
        
        @Override
        public void set(Vector3f vec, float value) {
            vec.setY(value);
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
        
        @Override
        public Vec3i mirror(Vec3i vec) {
            return new Vec3i(vec.getX(), -vec.getY(), vec.getZ());
        }
        
        @Override
        public BlockPos mirror(BlockPos vec) {
            return new BlockPos(vec.getX(), -vec.getY(), vec.getZ());
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
        public int get(Vec3i vec) {
            return vec.getZ();
        }
        
        @Override
        public Vec3i set(Vec3i vec, int value) {
            return new Vec3i(vec.getX(), vec.getY(), value);
        }
        
        @Override
        public BlockPos set(BlockPos pos, int value) {
            return new BlockPos(pos.getX(), pos.getY(), value);
        }
        
        @Override
        public void set(MutableBlockPos pos, int value) {
            pos.setZ(value);
        }
        
        @Override
        public SectionPos set(SectionPos pos, int value) {
            return SectionPos.of(pos.getX(), pos.getY(), value);
        }
        
        @Override
        public int get(ChunkPos pos) {
            return pos.z;
        }
        
        @Override
        public ChunkPos set(ChunkPos pos, int value) {
            return new ChunkPos(pos.x, value);
        }
        
        @Override
        public double get(Vec3 vec) {
            return vec.z;
        }
        
        @Override
        public Vec3 set(Vec3 vec, double value) {
            return new Vec3(vec.x, vec.y, value);
        }
        
        @Override
        public double get(Vector3d vec) {
            return vec.z;
        }
        
        @Override
        public void set(Vector3d vec, double value) {
            vec.z = value;
        }
        
        @Override
        public float get(Vector3f vec) {
            return vec.z();
        }
        
        @Override
        public void set(Vector3f vec, float value) {
            vec.setZ(value);;
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
        
        @Override
        public Vec3i mirror(Vec3i vec) {
            return new Vec3i(vec.getX(), vec.getY(), -vec.getZ());
        }
        
        @Override
        public BlockPos mirror(BlockPos vec) {
            return new BlockPos(vec.getX(), vec.getY(), -vec.getZ());
        }
    };
    
    public static Axis get(Direction.Axis axis) {
        return switch (axis) {
            case X -> Axis.X;
            case Y -> Axis.Y;
            case Z -> Axis.Z;
            default -> throw new IllegalArgumentException();
        };
    }
    
    public static Axis third(Axis one, Axis two) {
        return switch (one) {
            case X -> {
                if (two == Axis.Y)
                    yield Axis.Z;
                yield Axis.Y;
            }
            case Y -> {
                if (two == Axis.X)
                    yield Axis.Z;
                yield Axis.X;
            }
            case Z -> {
                if (two == Axis.Y)
                    yield Axis.X;
                yield Axis.Y;
            }
        };
    }
    
    public static Axis getMirrorAxis(Mirror mirrorIn) {
        return switch (mirrorIn) {
            case FRONT_BACK -> Axis.X;
            case LEFT_RIGHT -> Axis.Z;
            default -> null;
        };
    }
    
    public abstract Axis one();
    
    public abstract Axis two();
    
    public abstract Facing facing(boolean positive);
    
    public abstract double get(double x, double y, double z);
    
    public abstract float get(float x, float y, float z);
    
    public abstract int get(int x, int y, int z);
    
    public abstract int get(Vec3i vec);
    
    public abstract Vec3i set(Vec3i vec, int value);
    
    public abstract BlockPos set(BlockPos pos, int value);
    
    public abstract void set(MutableBlockPos pos, int value);
    
    public abstract SectionPos set(SectionPos pos, int value);
    
    public abstract int get(ChunkPos pos);
    
    public abstract ChunkPos set(ChunkPos pos, int value);
    
    public abstract double get(Vec3 vec);
    
    public abstract Vec3 set(Vec3 vec, double value);
    
    public abstract double get(Vector3d vec);
    
    public abstract void set(Vector3d vec, double value);
    
    public abstract float get(Vector3f vec);
    
    public abstract void set(Vector3f vec, float value);
    
    public abstract <T> T get(T x, T y, T z);
    
    public abstract Direction.Axis toVanilla();
    
    public Facing mirror(Facing facing) {
        if (facing.axis == this)
            return facing.opposite();
        return facing;
    }
    
    public Direction mirror(Direction facing) {
        if (facing.getAxis() == this.toVanilla())
            return facing.getOpposite();
        return facing;
    }
    
    public abstract Vec3i mirror(Vec3i vec);
    
    public abstract BlockPos mirror(BlockPos vec);
    
    public void mirror(Vec3d vec) {
        vec.set(this, -vec.get(this));
    }
    
    public void mirror(Vec3f vec) {
        vec.set(this, -vec.get(this));
    }
    
}
