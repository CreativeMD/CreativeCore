package team.creative.creativecore.common.util.math.box;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.collision.CollidingPlane.PlaneCache;
import team.creative.creativecore.common.util.math.matrix.IVecOrigin;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class OBB extends CreativeAABB {
    
    public IVecOrigin origin;
    public PlaneCache cache;
    
    public void buildCache() {
        this.cache = new PlaneCache(this);
    }
    
    public OBB(IVecOrigin origin, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
        this.origin = origin;
    }
    
    public OBB(IVecOrigin origin, AABB bb) {
        super(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        this.origin = origin;
    }
    
    @Override
    public OBB setMinX(double value) {
        return new OBB(origin, value, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }
    
    @Override
    public OBB setMinY(double value) {
        return new OBB(origin, this.minX, value, this.minZ, this.maxX, this.maxY, this.maxZ);
    }
    
    @Override
    public OBB setMinZ(double value) {
        return new OBB(origin, this.minX, this.minY, value, this.maxX, this.maxY, this.maxZ);
    }
    
    @Override
    public OBB setMaxX(double value) {
        return new OBB(origin, this.minX, this.minY, this.minZ, value, this.maxY, this.maxZ);
    }
    
    @Override
    public OBB setMaxY(double value) {
        return new OBB(origin, this.minX, this.minY, this.minZ, this.maxX, value, this.maxZ);
    }
    
    @Override
    public OBB setMaxZ(double value) {
        return new OBB(origin, this.minX, this.minY, this.minZ, this.maxX, this.maxY, value);
    }
    
    public OBB set(Facing facing, double value) {
        switch (facing) {
            case EAST:
                return new OBB(origin, this.minX, this.minY, this.minZ, value, this.maxY, this.maxZ);
            case WEST:
                return new OBB(origin, value, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
            case UP:
                return new OBB(origin, this.minX, this.minY, this.minZ, this.maxX, value, this.maxZ);
            case DOWN:
                return new OBB(origin, this.minX, value, this.minZ, this.maxX, this.maxY, this.maxZ);
            case SOUTH:
                return new OBB(origin, this.minX, this.minY, this.minZ, this.maxX, this.maxY, value);
            case NORTH:
                return new OBB(origin, this.minX, this.minY, value, this.maxX, this.maxY, this.maxZ);
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof OBB)) {
            return false;
        } else {
            OBB axisalignedbb = (OBB) p_equals_1_;
            
            if (axisalignedbb.origin != origin) {
                return false;
            } else if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
                return false;
            } else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
                return false;
            } else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
                return false;
            } else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
                return false;
            } else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
                return false;
            } else {
                return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
            }
        }
    }
    
    @Override
    public OBB contract(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        
        if (x < 0.0D) {
            d0 -= x;
        } else if (x > 0.0D) {
            d3 -= x;
        }
        
        if (y < 0.0D) {
            d1 -= y;
        } else if (y > 0.0D) {
            d4 -= y;
        }
        
        if (z < 0.0D) {
            d2 -= z;
        } else if (z > 0.0D) {
            d5 -= z;
        }
        
        return new OBB(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OBB expandTowards(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        
        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }
        
        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }
        
        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }
        
        return new OBB(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OBB inflate(double x, double y, double z) {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new OBB(origin, d0, d1, d2, d3, d4, d5);
    }
    
    @Override
    public OBB inflate(double value) {
        return this.inflate(value, value, value);
    }
    
    @Override
    public OBB move(double x, double y, double z) {
        return new OBB(origin, this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
    
    @Override
    public OBB move(BlockPos pos) {
        return new OBB(origin, this.minX + pos.getX(), this.minY + pos.getY(), this.minZ + pos.getZ(), this.maxX + pos.getX(), this.maxY + pos.getY(), this.maxZ + pos.getZ());
    }
    
    public static final float EPSILON = 0.001F;
    
    public static boolean smallerThanAndEquals(double a, double b) {
        return a < b || equals(a, b);
    }
    
    public static boolean greaterThanAndEquals(double a, double b) {
        return a > b || equals(a, b);
    }
    
    public static boolean equals(double a, double b) {
        return a == b ? true : Math.abs(a - b) < EPSILON;
    }
    
    public double getMaxTranslated(Axis axis) {
        return getMax(axis) + origin.translationCombined(axis);
    }
    
    public double getMinTranslated(Axis axis) {
        return getMin(axis) + origin.translationCombined(axis);
    }
    
    @Override
    public boolean intersects(AABB other) {
        if (other instanceof OBB) {
            if (((OBB) other).origin == origin)
                return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ;
            else {
                OBB converted = ((OBB) other).origin.getOrientatedBox(origin.getAxisAlignedBox(this));
                return converted.minX < other.maxX && converted.maxX > other.minX && converted.minY < other.maxY && converted.maxY > other.minY && converted.minZ < other.maxZ && converted.maxZ > other.minZ;
            }
        }
        
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }
    
    @Override
    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        AABB box = origin.getAxisAlignedBox(this);
        return box.minX < maxX && box.maxX > minX && box.minY < maxY && box.maxY > minY && box.minZ < maxZ && box.maxZ > minZ;
    }
    
    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
    public Vec3d getCenter3d() {
        return new Vec3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }
    
    public double getPushOutScale(double minScale, OBB fakeBox, Vec3d pushVec) {
        double scale = Double.MAX_VALUE;
        
        boolean pushX = pushVec.x != 0;
        boolean pushY = pushVec.y != 0;
        boolean pushZ = pushVec.z != 0;
        
        if (pushX)
            if (pushVec.x > 0)
                scale = Math.min(scale, Math.abs((this.maxX - fakeBox.minX) / pushVec.x));
            else
                scale = Math.min(scale, Math.abs((this.minX - fakeBox.maxX) / pushVec.x));
            
        if (pushY)
            if (pushVec.y > 0)
                scale = Math.min(scale, Math.abs((this.maxY - fakeBox.minY) / pushVec.y));
            else
                scale = Math.min(scale, Math.abs((this.minY - fakeBox.maxY) / pushVec.y));
            
        if (pushZ)
            if (pushVec.z > 0)
                scale = Math.min(scale, Math.abs((this.maxZ - fakeBox.minZ) / pushVec.z));
            else
                scale = Math.min(scale, Math.abs((this.minZ - fakeBox.maxZ) / pushVec.z));
            
        if (scale <= minScale)
            return minScale;
        
        return scale;
        
    }
    
}
