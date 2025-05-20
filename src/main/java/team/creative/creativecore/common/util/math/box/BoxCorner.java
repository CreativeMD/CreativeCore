package team.creative.creativecore.common.util.math.box;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.matrix.IntMatrix3c;
import team.creative.creativecore.common.util.math.transformation.Rotation;
import team.creative.creativecore.common.util.math.utils.BooleanUtils;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public enum BoxCorner {
    EUN(Facing.EAST, Facing.UP, Facing.NORTH),
    EUS(Facing.EAST, Facing.UP, Facing.SOUTH),
    EDN(Facing.EAST, Facing.DOWN, Facing.NORTH),
    EDS(Facing.EAST, Facing.DOWN, Facing.SOUTH),
    WUN(Facing.WEST, Facing.UP, Facing.NORTH),
    WUS(Facing.WEST, Facing.UP, Facing.SOUTH),
    WDN(Facing.WEST, Facing.DOWN, Facing.NORTH),
    WDS(Facing.WEST, Facing.DOWN, Facing.SOUTH);
    
    // Direct lookup by bitmask: (x>0?1:0)<<2 | (y>0?1:0)<<1 | (z>0?1:0)
    private static final BoxCorner[] LOOKUP = new BoxCorner[8];
    
    public static final BoxCorner[][] FACING_CORNERS = new BoxCorner[][] { { EDN, EDS, WDN, WDS }, // DOWN
            { EUN, EUS, WUN, WUS }, // UP
            { EUN, EDN, WUN, WDN }, // NORTH
            { EUS, EDS, WUS, WDS }, // SOUTH
            { WUN, WUS, WDN, WDS }, // WEST
            { EUN, EUS, EDN, EDS } // EAST
    };
    
    public final Facing x;
    public final Facing y;
    public final Facing z;
    
    public BoxCorner neighborOne;
    public BoxCorner neighborTwo;
    public BoxCorner neighborThree;
    
    private BoxCorner(Facing x, Facing y, Facing z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    static {
        // Populate direct lookup table
        for (BoxCorner c : values()) {
            int mask = ((c.x.offset() > 0 ? 1 : 0) << 2) | ((c.y.offset() > 0 ? 1 : 0) << 1) | (c.z.offset() > 0 ? 1 : 0);
            LOOKUP[mask] = c;
        }
        // Initialize neighbors
        for (BoxCorner c : values()) {
            c.neighborOne = lookup(c.x.opposite(), c.y, c.z);
            c.neighborTwo = lookup(c.x, c.y.opposite(), c.z);
            c.neighborThree = lookup(c.x, c.y, c.z.opposite());
        }
    }
    
    private static BoxCorner lookup(Facing x, Facing y, Facing z) {
        int mask = ((x.offset() > 0 ? 1 : 0) << 2) | ((y.offset() > 0 ? 1 : 0) << 1) | (z.offset() > 0 ? 1 : 0);
        return LOOKUP[mask];
    }
    
    public static BoxCorner getCornerUnsorted(Facing a, Facing b, Facing c) {
        Facing x = a.axis == Axis.X ? a : (b.axis == Axis.X ? b : c);
        Facing y = a.axis == Axis.Y ? a : (b.axis == Axis.Y ? b : c);
        Facing z = a.axis == Axis.Z ? a : (b.axis == Axis.Z ? b : c);
        return getCorner(x, y, z);
    }
    
    public static BoxCorner getCorner(Facing x, Facing y, Facing z) {
        // Direct lookup instead of loop
        int mask = ((x.offset() > 0 ? 1 : 0) << 2) | ((y.offset() > 0 ? 1 : 0) << 1) | (z.offset() > 0 ? 1 : 0);
        return LOOKUP[mask];
    }
    
    public static BoxCorner[] faceCorners(Facing facing) {
        return FACING_CORNERS[facing.ordinal()];
    }
    
    public boolean isFacing(Facing facing) {
        return getFacing(facing.axis) == facing;
    }
    
    public boolean isFacingPositive(Axis axis) {
        return getFacing(axis).positive;
    }
    
    public Facing getFacing(Axis axis) {
        return switch (axis) {
            case X -> x;
            case Y -> y;
            case Z -> z;
        };
    }
    
    public BoxCorner transform(IntMatrix3c matrix) {
        int ox = x.offset(), oy = y.offset(), oz = z.offset();
        return getCorner(Facing.get(Axis.X, matrix.getX(ox, oy, oz) > 0), Facing.get(Axis.Y, matrix.getY(ox, oy, oz) > 0), Facing.get(Axis.Z, matrix.getZ(ox, oy, oz) > 0));
    }
    
    public BoxCorner mirror(Axis axis) {
        return switch (axis) {
            case X -> lookup(x.opposite(), y, z);
            case Y -> lookup(x, y.opposite(), z);
            case Z -> lookup(x, y, z.opposite());
        };
    }
    
    public BoxCorner rotate(Rotation rotation) {
        int ox = x.offset(), oy = y.offset(), oz = z.offset();
        var m = rotation.getMatrix();
        return getCorner(Facing.get(Axis.X, m.getX(ox, oy, oz) > 0), Facing.get(Axis.Y, m.getY(ox, oy, oz) > 0), Facing.get(Axis.Z, m.getZ(ox, oy, oz) > 0));
    }
    
    public Vec3d get(ABB bb) {
        return new Vec3d(bb.get(x), bb.get(y), bb.get(z));
    }
    
    public void set(ABB bb, Vec3d vec) {
        vec.x = bb.get(x);
        vec.y = bb.get(y);
        vec.z = bb.get(z);
    }
    
    public Vec3d get(AABB bb) {
        return new Vec3d(BoxUtils.get(bb, x), BoxUtils.get(bb, y), BoxUtils.get(bb, z));
    }
    
    public void set(AABB bb, Vec3d vec) {
        vec.x = BoxUtils.get(bb, x);
        vec.y = BoxUtils.get(bb, y);
        vec.z = BoxUtils.get(bb, z);
    }
    
    public Facing facingTo(BoxCorner corner) {
        boolean sx = this.x == corner.x;
        boolean sy = this.y == corner.y;
        boolean sz = this.z == corner.z;
        if (!BooleanUtils.explicitOneFalse(sx, sy, sz))
            return null;
        if (!sx)
            return corner.x;
        if (!sy)
            return corner.y;
        return corner.z;
    }
}