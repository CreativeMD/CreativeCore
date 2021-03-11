package com.creativemd.creativecore.common.utils.math.box;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.VectorUtils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;

public enum BoxFace {
    
    EAST(
        EnumFacing.EAST,
        new BoxCorner[] { BoxCorner.EUS, BoxCorner.EDS, BoxCorner.EDN, BoxCorner.EUN },
        EnumFacing.NORTH,
        EnumFacing.DOWN),
    WEST(
        EnumFacing.WEST,
        new BoxCorner[] { BoxCorner.WUN, BoxCorner.WDN, BoxCorner.WDS, BoxCorner.WUS },
        EnumFacing.SOUTH,
        EnumFacing.DOWN),
    UP(
        EnumFacing.UP,
        new BoxCorner[] { BoxCorner.WUN, BoxCorner.WUS, BoxCorner.EUS, BoxCorner.EUN },
        EnumFacing.EAST,
        EnumFacing.SOUTH),
    DOWN(
        EnumFacing.DOWN,
        new BoxCorner[] { BoxCorner.WDS, BoxCorner.WDN, BoxCorner.EDN, BoxCorner.EDS },
        EnumFacing.EAST,
        EnumFacing.NORTH),
    SOUTH(
        EnumFacing.SOUTH,
        new BoxCorner[] { BoxCorner.WUS, BoxCorner.WDS, BoxCorner.EDS, BoxCorner.EUS },
        EnumFacing.EAST,
        EnumFacing.DOWN),
    NORTH(
        EnumFacing.NORTH,
        new BoxCorner[] { BoxCorner.EUN, BoxCorner.EDN, BoxCorner.WDN, BoxCorner.WUN },
        EnumFacing.WEST,
        EnumFacing.DOWN);
    
    public final EnumFacing facing;
    public final BoxCorner[] corners;
    
    private final Axis one;
    private final Axis two;
    
    private final EnumFacing texU;
    private final EnumFacing texV;
    
    private final BoxTriangle triangleFirst;
    private final BoxTriangle triangleFirstInv;
    private final BoxTriangle triangleSecond;
    private final BoxTriangle triangleSecondInv;
    
    BoxFace(EnumFacing facing, BoxCorner[] corners, EnumFacing texU, EnumFacing texV) {
        this.facing = facing;
        this.corners = corners;
        
        this.one = RotationUtils.getOne(facing.getAxis());
        this.two = RotationUtils.getTwo(facing.getAxis());
        
        this.texU = texU;
        this.texV = texV;
        
        this.triangleFirst = new BoxTriangle(new BoxCorner[] { corners[0], corners[1], corners[2] });
        this.triangleFirstInv = new BoxTriangle(new BoxCorner[] { corners[0], corners[1], corners[3] });
        this.triangleSecond = new BoxTriangle(new BoxCorner[] { corners[0], corners[2], corners[3] });
        this.triangleSecondInv = new BoxTriangle(new BoxCorner[] { corners[1], corners[2], corners[3] });
    }
    
    private BoxTriangle getTriangle(boolean first, boolean inverted) {
        if (first)
            if (inverted)
                return triangleFirstInv;
            else
                return triangleFirst;
        else if (inverted)
            return triangleSecondInv;
        return triangleSecond;
    }
    
    public Axis getTexUAxis() {
        return texU.getAxis();
    }
    
    public Axis getTexVAxis() {
        return texV.getAxis();
    }
    
    public EnumFacing getTexU() {
        return texU;
    }
    
    public EnumFacing getTexV() {
        return texV;
    }
    
    public BoxCorner[] getTriangleFirst(boolean inverted) {
        return getTriangle(true, inverted).corners;
    }
    
    public BoxCorner[] getTriangleSecond(boolean inverted) {
        return getTriangle(false, inverted).corners;
    }
    
    public Vector3d first(Vector3d[] corners) {
        return corners[this.corners[0].ordinal()];
    }
    
    public Vector3d normal(Vector3d[] corners) {
        Vector3d origin = first(corners);
        Vector3d first = new Vector3d(corners[this.corners[1].ordinal()]);
        Vector3d second = new Vector3d(corners[this.corners[2].ordinal()]);
        first.sub(origin);
        second.sub(origin);
        return new Vector3d(first.y * second.z - first.z * second.y, first.z * second.x - first.x * second.z, first.x * second.y - first.y * second.x);
    }
    
    public Boolean isFacingOutwards(boolean first, boolean inverted, Vector3f normal) {
        BoxTriangle triangle = getTriangle(first, inverted);
        float valueOne = VectorUtils.get(one, normal);
        float valueTwo = VectorUtils.get(two, normal);
        Boolean outwardOne = valueOne == 0 ? null : valueOne > 0;
        Boolean outwardTwo = valueTwo == 0 ? null : valueTwo > 0;
        
        if (outwardOne == outwardTwo)
            return outwardOne;
        if (outwardOne != null && outwardTwo == null)
            return outwardOne;
        if (outwardOne == null && outwardTwo != null)
            return outwardTwo;
        
        if (valueOne == valueTwo)
            return null;
        if (valueOne > valueTwo)
            return outwardOne;
        return outwardTwo;
    }
    
    public static Vector3f getTraingleNormal(BoxCorner[] triangle, Vector3f[] corners) {
        Vector3f a = new Vector3f(corners[triangle[1].ordinal()]);
        a.sub(corners[triangle[0].ordinal()]);
        
        Vector3f b = new Vector3f(corners[triangle[2].ordinal()]);
        b.sub(corners[triangle[0].ordinal()]);
        
        Vector3f normal = new Vector3f();
        normal.cross(a, b);
        return normal;
    }
    
    public static void ensureSameLength(Vector3f one, Vector3f two) {
        float normVecOne = one.x * one.x + one.y * one.y + one.z * one.z;
        float normVecTwo = two.x * two.x + two.y * two.y + two.z * two.z;
        one.scale(normVecTwo);
        two.scale(normVecOne);
    }
    
    public static BoxFace get(EnumFacing facing) {
        switch (facing) {
        case EAST:
            return EAST;
        case WEST:
            return WEST;
        case UP:
            return UP;
        case DOWN:
            return DOWN;
        case SOUTH:
            return SOUTH;
        case NORTH:
            return NORTH;
        default:
            return null;
        }
    }
    
    public static BoxFace get(Axis axis, boolean direction) {
        switch (axis) {
        case X:
            return direction ? EAST : WEST;
        case Y:
            return direction ? UP : DOWN;
        case Z:
            return direction ? SOUTH : NORTH;
        default:
            return null;
        }
    }
    
    public static Vector3f[] getVecArray(BoxCorner[] corners, Vector3f[] vecs) {
        Vector3f[] result = new Vector3f[corners.length];
        for (int i = 0; i < result.length; i++)
            result[i] = vecs[corners[i].ordinal()];
        return result;
    }
    
    private class BoxTriangle {
        
        private final BoxCorner[] corners;
        private final boolean outwardDirectionOne;
        private final boolean outwardDirectionTwo;
        
        public BoxTriangle(BoxCorner[] corners) {
            this.corners = corners;
            this.outwardDirectionOne = corners[1].getFacing(one).getAxisDirection() == AxisDirection.POSITIVE;
            this.outwardDirectionTwo = corners[1].getFacing(two).getAxisDirection() == AxisDirection.POSITIVE;
        }
        
    }
}