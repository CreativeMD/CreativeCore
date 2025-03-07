package team.creative.creativecore.common.util.math.transformation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.matrix.IntMatrix3;
import team.creative.creativecore.common.util.math.matrix.IntMatrix3c;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;
import team.creative.creativecore.common.util.math.vec.VectorUtils;

public enum Rotation {
    
    X_CLOCKWISE(Axis.X, new IntMatrix3(1, 0, 0, 0, 0, -1, 0, 1, 0), true) {
        @Override
        public Rotation opposite() {
            return X_COUNTER_CLOCKWISE;
        }
    },
    
    X_COUNTER_CLOCKWISE(Axis.X, new IntMatrix3(1, 0, 0, 0, 0, 1, 0, -1, 0), false) {
        @Override
        public Rotation opposite() {
            return X_CLOCKWISE;
        }
    },
    
    Y_CLOCKWISE(Axis.Y, new IntMatrix3(0, 0, 1, 0, 1, 0, -1, 0, 0), true) {
        @Override
        public Rotation opposite() {
            return Rotation.Y_COUNTER_CLOCKWISE;
        }
    },
    Y_COUNTER_CLOCKWISE(Axis.Y, new IntMatrix3(0, 0, -1, 0, 1, 0, 1, 0, 0), false) {
        @Override
        public Rotation opposite() {
            return Rotation.Y_CLOCKWISE;
        }
    },
    
    Z_CLOCKWISE(Axis.Z, new IntMatrix3(0, -1, 0, 1, 0, 0, 0, 0, 1), true) {
        @Override
        public Rotation opposite() {
            return Rotation.Z_COUNTER_CLOCKWISE;
        }
    },
    Z_COUNTER_CLOCKWISE(Axis.Z, new IntMatrix3(0, 1, 0, -1, 0, 0, 0, 0, 1), false) {
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
    
    public static Rotation ofNormal(Vec3i vec) {
        if (vec.getX() > 0)
            return Rotation.X_CLOCKWISE;
        if (vec.getX() < 0)
            return Rotation.X_COUNTER_CLOCKWISE;
        if (vec.getY() > 0)
            return Rotation.Y_CLOCKWISE;
        if (vec.getY() < 0)
            return Rotation.Y_COUNTER_CLOCKWISE;
        if (vec.getZ() > 0)
            return Rotation.Z_CLOCKWISE;
        if (vec.getZ() < 0)
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
    private final Vec3i normal;
    private final IntMatrix3c rotMatrix;
    
    private Rotation(Axis axis, IntMatrix3c matrix, boolean clockwise) {
        this.rotMatrix = matrix;
        this.axis = axis;
        this.clockwise = clockwise;
        this.direction = clockwise ? 1 : -1;
        this.normal = VectorUtils.set(Vec3i.ZERO, direction, axis);
    }
    
    public IntMatrix3c getMatrix() {
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
        return Rotation.ofNormal(by.getMatrix().transform(normal));
    }
    
    public Rotation transform(IntMatrix3c matrix) {
        return Rotation.ofNormal(matrix.transform(normal));
    }
    
    public BlockPos transform(BlockPos vec) {
        return rotMatrix.transform(vec);
    }
    
    public Vec3i transform(Vec3i vec) {
        return rotMatrix.transform(vec);
    }
    
    public Vec3 transform(Vec3 vec) {
        return rotMatrix.transform(vec);
    }
    
    public void transform(Vec3d vec) {
        rotMatrix.transform(vec);
    }
    
    public void transform(Vec3f vec) {
        rotMatrix.transform(vec);
    }
    
}
