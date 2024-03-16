package team.creative.creativecore.common.util.math.transformation;

import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public enum Rotation {
    
    X_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0, 0, 0, -1, 0, 1, 0), true) {
        @Override
        public Rotation opposite() {
            return X_COUNTER_CLOCKWISE;
        }
    },
    
    X_COUNTER_CLOCKWISE(Axis.X, new RotationMatrix(1, 0, 0, 0, 0, 1, 0, -1, 0), false) {
        @Override
        public Rotation opposite() {
            return X_CLOCKWISE;
        }
    },
    
    Y_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, 1, 0, 1, 0, -1, 0, 0), true) {
        @Override
        public Rotation opposite() {
            return Rotation.Y_COUNTER_CLOCKWISE;
        }
    },
    Y_COUNTER_CLOCKWISE(Axis.Y, new RotationMatrix(0, 0, -1, 0, 1, 0, 1, 0, 0), false) {
        @Override
        public Rotation opposite() {
            return Rotation.Y_CLOCKWISE;
        }
    },
    
    Z_CLOCKWISE(Axis.Z, new RotationMatrix(0, -1, 0, 1, 0, 0, 0, 0, 1), true) {
        @Override
        public Rotation opposite() {
            return Rotation.Z_COUNTER_CLOCKWISE;
        }
    },
    Z_COUNTER_CLOCKWISE(Axis.Z, new RotationMatrix(0, 1, 0, -1, 0, 0, 0, 0, 1), false) {
        @Override
        public Rotation opposite() {
            return Z_CLOCKWISE;
        }
    };
    
    private static final Facing[][] FACING_ROTATION = new Facing[6][6];
    
    static {
        for (int i = 0; i < FACING_ROTATION.length; i++) {
            Facing facing = Facing.get(i);
            for (int j = 0; j < Rotation.values().length; j++) {
                Rotation rotation = Rotation.values()[j];
                Vec3i rotatedNormal = new Vec3i(rotation.getMatrix().getX(facing.normal), rotation.getMatrix().getY(facing.normal), rotation.getMatrix().getZ(facing.normal));
                for (int k = 0; k < Facing.values().length; k++) {
                    if (Facing.get(k).normal.equals(rotatedNormal)) {
                        FACING_ROTATION[i][j] = Facing.get(k);
                        break;
                    }
                }
            }
        }
        
    }
    
    public static Rotation getRotation(Axis axis, boolean clockwise) {
        return switch (axis) {
            case X -> clockwise ? X_CLOCKWISE : X_COUNTER_CLOCKWISE;
            case Y -> clockwise ? Y_CLOCKWISE : Y_COUNTER_CLOCKWISE;
            case Z -> clockwise ? Z_CLOCKWISE : Z_COUNTER_CLOCKWISE;
        };
    }
    
    public static Rotation getRotation(Vec3d vec) {
        if (vec.x > 0)
            return Rotation.X_CLOCKWISE;
        if (vec.x < 0)
            return Rotation.X_COUNTER_CLOCKWISE;
        if (vec.y > 0)
            return Rotation.Y_CLOCKWISE;
        if (vec.y < 0)
            return Rotation.Y_COUNTER_CLOCKWISE;
        if (vec.z > 0)
            return Rotation.Z_CLOCKWISE;
        if (vec.z < 0)
            return Rotation.Z_COUNTER_CLOCKWISE;
        return null;
    }
    
    public static Rotation getRotation(net.minecraft.world.level.block.Rotation rotationIn) {
        return switch (rotationIn) {
            case CLOCKWISE_90 -> Rotation.Y_CLOCKWISE;
            case CLOCKWISE_180 -> Rotation.Y_CLOCKWISE;
            case COUNTERCLOCKWISE_90 -> Rotation.Y_COUNTER_CLOCKWISE;
            default -> null;
        };
    }
    
    public static int getRotationCount(net.minecraft.world.level.block.Rotation rotationIn) {
        if (rotationIn == net.minecraft.world.level.block.Rotation.CLOCKWISE_180)
            return 2;
        return 1;
    }
    
    public final Axis axis;
    public final int direction;
    public final boolean clockwise;
    private final Vec3d vec;
    private final RotationMatrix rotMatrix;
    
    private Rotation(Axis axis, RotationMatrix matrix, boolean clockwise) {
        this.rotMatrix = matrix;
        this.axis = axis;
        this.clockwise = clockwise;
        this.direction = clockwise ? 1 : -1;
        this.vec = new Vec3d();
        this.vec.set(axis, direction);
    }
    
    public boolean getRotatedComponentPositive(Axis axis) {
        switch (axis) {
            case X -> {
                if (rotMatrix.m00 != 0)
                    return rotMatrix.m00 > 0;
                else if (rotMatrix.m10 != 0)
                    return rotMatrix.m10 > 0;
                else
                    return rotMatrix.m20 > 0;
            }
            case Y -> {
                if (rotMatrix.m01 != 0)
                    return rotMatrix.m01 > 0;
                else if (rotMatrix.m11 != 0)
                    return rotMatrix.m11 > 0;
                else
                    return rotMatrix.m21 > 0;
            }
            case Z -> {
                if (rotMatrix.m02 != 0)
                    return rotMatrix.m02 > 0;
                else if (rotMatrix.m12 != 0)
                    return rotMatrix.m12 > 0;
                else
                    return rotMatrix.m22 > 0;
            }
        }
        return true;
    }
    
    public Axis getRotatedComponent(Axis axis) {
        switch (axis) {
            case X -> {
                if (rotMatrix.m00 != 0)
                    return Axis.X;
                else if (rotMatrix.m10 != 0)
                    return Axis.Y;
                else
                    return Axis.Z;
            }
            case Y -> {
                if (rotMatrix.m01 != 0)
                    return Axis.X;
                else if (rotMatrix.m11 != 0)
                    return Axis.Y;
                else
                    return Axis.Z;
            }
            case Z -> {
                if (rotMatrix.m02 != 0)
                    return Axis.X;
                else if (rotMatrix.m12 != 0)
                    return Axis.Y;
                else
                    return Axis.Z;
            }
        }
        return axis;
    }
    
    public boolean negativeX() {
        return rotMatrix.m00 != 0 ? rotMatrix.m00 < 0 : (rotMatrix.m01 != 0 ? rotMatrix.m01 < 0 : rotMatrix.m02 < 0);
    }
    
    public <T> T getX(T x, T y, T z) {
        return rotMatrix.m00 != 0 ? x : (rotMatrix.m01 != 0 ? y : z);
    }
    
    public boolean negativeY() {
        return rotMatrix.m10 != 0 ? rotMatrix.m10 < 0 : (rotMatrix.m11 != 0 ? rotMatrix.m11 < 0 : rotMatrix.m12 < 0);
    }
    
    public <T> T getY(T x, T y, T z) {
        return rotMatrix.m10 != 0 ? x : (rotMatrix.m11 != 0 ? y : z);
    }
    
    public boolean negativeZ() {
        return rotMatrix.m20 != 0 ? rotMatrix.m20 < 0 : (rotMatrix.m21 != 0 ? rotMatrix.m21 < 0 : rotMatrix.m22 < 0);
    }
    
    public <T> T getZ(T x, T y, T z) {
        return rotMatrix.m20 != 0 ? x : (rotMatrix.m21 != 0 ? y : z);
    }
    
    public Vec3d getVec() {
        return new Vec3d(vec);
    }
    
    public RotationMatrix getMatrix() {
        return this.rotMatrix;
    }
    
    public abstract Rotation opposite();
    
    public Facing rotate(Facing facing) {
        return FACING_ROTATION[facing.ordinal()][this.ordinal()];
    }
    
    public Axis rotate(Axis axis) {
        if (axis == this.axis)
            return axis;

        return switch (axis) {
            case X -> this.axis == Axis.Y ? Axis.Z : Axis.Y;
            case Y -> this.axis == Axis.Z ? Axis.X : Axis.Y;
            case Z -> this.axis == Axis.X ? Axis.Y : Axis.X;
        };
    }
    
    public Rotation mirror(Axis axis) {
        return this.axis == axis ? opposite() : this;
    }
    
    public Rotation rotate(Rotation by) {
        Vec3d vec = getVec();
        by.getMatrix().transform(vec);
        return Rotation.getRotation(vec);
    }
    
    public BlockPos transform(BlockPos vec) {
        return rotMatrix.transform(vec);
    }
    
    public Vec3i transform(Vec3i vec) {
        return rotMatrix.transform(vec);
    }
    
    public Vector3d transform(Vector3d vec) {
        return rotMatrix.transform(vec);
    }
    
    public void transform(Vec3d vec) {
        rotMatrix.transform(vec);
    }
    
    public void transform(Vec3f vec) {
        rotMatrix.transform(vec);
    }
    
    public static class RotationMatrix {
        
        public int m00;
        public int m01;
        public int m02;
        public int m10;
        public int m11;
        public int m12;
        public int m20;
        public int m21;
        public int m22;
        
        public RotationMatrix(int m00, int m01, int m02, int m10, int m11, int m12, int m20, int m21, int m22) {
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
        
        public RotationMatrix(RotationMatrix m1, RotationMatrix m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        }
        
        public int getX(int[] vec) {
            return getX(vec[0], vec[1], vec[2]);
        }
        
        public int getX(Vec3i vec) {
            return getX(vec.getX(), vec.getY(), vec.getZ());
        }
        
        public int getX(int x, int y, int z) {
            return x * m00 + y * m01 + z * m02;
        }
        
        public long getX(long x, long y, long z) {
            return x * m00 + y * m01 + z * m02;
        }
        
        public int getY(int[] vec) {
            return getY(vec[0], vec[1], vec[2]);
        }
        
        public int getY(Vec3i vec) {
            return getY(vec.getX(), vec.getY(), vec.getZ());
        }
        
        public int getY(int x, int y, int z) {
            return x * m10 + y * m11 + z * m12;
        }
        
        public long getY(long x, long y, long z) {
            return x * m10 + y * m11 + z * m12;
        }
        
        public int getZ(int[] vec) {
            return getZ(vec[0], vec[1], vec[2]);
        }
        
        public int getZ(Vec3i vec) {
            return getZ(vec.getX(), vec.getY(), vec.getZ());
        }
        
        public int getZ(int x, int y, int z) {
            return x * m20 + y * m21 + z * m22;
        }
        
        public long getZ(long x, long y, long z) {
            return x * m20 + y * m21 + z * m22;
        }
        
        public BlockPos transform(BlockPos vec) {
            int x = vec.getX() * m00 + vec.getY() * m01 + vec.getZ() * m02;
            int y = vec.getX() * m10 + vec.getY() * m11 + vec.getZ() * m12;
            int z = vec.getX() * m20 + vec.getY() * m21 + vec.getZ() * m22;
            return new BlockPos(x, y, z);
        }
        
        public Vec3i transform(Vec3i vec) {
            int x = vec.getX() * m00 + vec.getY() * m01 + vec.getZ() * m02;
            int y = vec.getX() * m10 + vec.getY() * m11 + vec.getZ() * m12;
            int z = vec.getX() * m20 + vec.getY() * m21 + vec.getZ() * m22;
            return new Vec3i(x, y, z);
        }
        
        public Vector3d transform(Vector3d vec) {
            double x = vec.x * m00 + vec.y * m01 + vec.z * m02;
            double y = vec.x * m10 + vec.y * m11 + vec.z * m12;
            double z = vec.x * m20 + vec.y * m21 + vec.z * m22;
            return new Vector3d(x, y, z);
        }
        
        public void transform(Vec3d triple) {
            double x = triple.x * m00 + triple.y * m01 + triple.z * m02;
            double y = triple.x * m10 + triple.y * m11 + triple.z * m12;
            double z = triple.x * m20 + triple.y * m21 + triple.z * m22;
            triple.set(x, y, z);
        }
        
        public void transform(Vec3f triple) {
            float x = triple.x * m00 + triple.y * m01 + triple.z * m02;
            float y = triple.x * m10 + triple.y * m11 + triple.z * m12;
            float z = triple.x * m20 + triple.y * m21 + triple.z * m22;
            triple.set(x, y, z);
        }
        
        @Override
        public boolean equals(Object object) {
            if (object instanceof RotationMatrix m1) {
                return (this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
        }
        
    }
    
}
