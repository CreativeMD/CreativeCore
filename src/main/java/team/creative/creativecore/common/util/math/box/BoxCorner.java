package team.creative.creativecore.common.util.math.box;

import net.minecraft.world.phys.AABB;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.math.transformation.Rotation;
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
    
    public final Facing x;
    public final Facing y;
    public final Facing z;
    
    public BoxCorner neighborOne;
    public BoxCorner neighborTwo;
    public BoxCorner neighborThree;
    
    public static final BoxCorner[][] FACING_CORNERS = new BoxCorner[][] { { EDN, EDS, WDN, WDS }, { EUN, EUS, WUN, WUS }, { EUN, EDN, WUN, WDN }, { EUS, EDS, WUS, WDS }, { WUN, WUS, WDN, WDS }, { EUN, EUS, EDN, EDS } };
    
    private BoxCorner(Facing x, Facing y, Facing z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    private void init() {
        neighborOne = getCorner(x.opposite(), y, z);
        neighborTwo = getCorner(x, y.opposite(), z);
        neighborThree = getCorner(x, y, z.opposite());
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
    
    public BoxCorner mirror(Axis axis) {
        return switch (axis) {
            case X -> getCorner(x.opposite(), y, z);
            case Y -> getCorner(x, y.opposite(), z);
            case Z -> getCorner(x, y, z.opposite());
        };
    }
    
    public BoxCorner rotate(Rotation rotation) {
        int normalX = x.offset();
        int normalY = y.offset();
        int normalZ = z.offset();
        return getCorner(Facing.get(Axis.X, rotation.getMatrix().getX(normalX, normalY, normalZ) > 0), Facing.get(Axis.Y, rotation.getMatrix().getY(normalX, normalY, normalZ) > 0),
            Facing.get(Axis.Z, rotation.getMatrix().getZ(normalX, normalY, normalZ) > 0));
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
    
    public static BoxCorner getCornerUnsorted(Facing facing, Facing facing2, Facing facing3) {
        return getCorner(facing.axis != Axis.X ? facing2.axis != Axis.X ? facing3 : facing2 : facing, facing.axis != Axis.Y ? facing2.axis != Axis.Y ? facing3 : facing2 : facing,
            facing.axis != Axis.Z ? facing2.axis != Axis.Z ? facing3 : facing2 : facing);
    }
    
    public static BoxCorner getCorner(Facing x, Facing y, Facing z) {
        for (BoxCorner corner : BoxCorner.values()) {
            if (corner.x == x && corner.y == y && corner.z == z)
                return corner;
        }
        return null;
    }
    
    public static BoxCorner[] faceCorners(Facing facing) {
        return FACING_CORNERS[facing.ordinal()];
    }
    
    static {
        for (BoxCorner corner : BoxCorner.values())
            corner.init();
    }
}
