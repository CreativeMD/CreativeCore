package com.creativemd.creativecore.common.collision;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.utils.BoxUtils.BoxCorner;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeAxisAlignedBB extends AxisAlignedBB {
	
	public CreativeAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        super(x1, y1, z1, x2, y2, z2);
    }

    public CreativeAxisAlignedBB(BlockPos pos)
    {
    	super(pos);
    }

    public CreativeAxisAlignedBB(BlockPos pos1, BlockPos pos2)
    {
        super(pos1, pos2);
    }
    
    public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset)
    {
    	return calculateYOffset(other, offset);
    }
    
    protected double getValueOfFacing(EnumFacing facing)
	{
		switch(facing)
		{
		case EAST:
			return maxX;
		case WEST:
			return minX;
		case UP:
			return maxY;
		case DOWN:
			return minY;
		case SOUTH:
			return maxZ;
		case NORTH:
			return minZ;
		
		}
		return 0;
	}
    
    public Vec3d getCorner(BoxCorner corner)
	{
		return new Vec3d(getCornerX(corner), getCornerY(corner), getCornerZ(corner));
	}
	
	public double getCornerValue(BoxCorner corner, Axis axis)
	{
		return getValueOfFacing(corner.getFacing(axis));
	}
	
	public double getCornerX(BoxCorner corner)
	{
		return getValueOfFacing(corner.x);
	}
	
	public double getCornerY(BoxCorner corner)
	{
		return getValueOfFacing(corner.y);
	}
	
	public double getCornerZ(BoxCorner corner)
	{
		return getValueOfFacing(corner.z);
	}
	
	public Vec3d getSize()
	{
		return new Vec3d(maxX - minX, maxY - minY, maxZ - minZ);
	}
	
	public double getSize(Axis axis)
	{
		switch (axis)
		{
		case X:
			return maxX - minX;
		case Y:
			return maxY - minY;
		case Z:
			return maxZ - minZ;
		}
		return 0;
	}
	
	public double getMin(Axis axis)
	{
		switch (axis)
		{
		case X:
			return minX;
		case Y:
			return minY;
		case Z:
			return minZ;
		}
		return 0;
	}
	
	public double getMax(Axis axis)
	{
		switch (axis)
		{
		case X:
			return maxX;
		case Y:
			return maxY;
		case Z:
			return maxZ;
		}
		return 0;
	}
	
	public static double getValueOfFacing(AxisAlignedBB bb, EnumFacing facing)
	{
		switch(facing)
		{
		case EAST:
			return bb.maxX;
		case WEST:
			return bb.minX;
		case UP:
			return bb.maxY;
		case DOWN:
			return bb.minY;
		case SOUTH:
			return bb.maxZ;
		case NORTH:
			return bb.minZ;
		
		}
		return 0;
	}
	
	public static double getMin(AxisAlignedBB bb, Axis axis)
	{
		switch (axis)
		{
		case X:
			return bb.minX;
		case Y:
			return bb.minY;
		case Z:
			return bb.minZ;
		}
		return 0;
	}
	
	public static double getMax(AxisAlignedBB bb, Axis axis)
	{
		switch (axis)
		{
		case X:
			return bb.maxX;
		case Y:
			return bb.maxY;
		case Z:
			return bb.maxZ;
		}
		return 0;
	}
	
	public static Vec3d getCorner(AxisAlignedBB bb, BoxCorner corner)
	{
		return new Vec3d(getCornerX(bb, corner), getCornerY(bb, corner), getCornerZ(bb, corner));
	}
	
	public static double getCornerValue(AxisAlignedBB bb, BoxCorner corner, Axis axis)
	{
		return getValueOfFacing(bb, corner.getFacing(axis));
	}
	
	public static double getCornerX(AxisAlignedBB bb, BoxCorner corner)
	{
		return getValueOfFacing(bb, corner.x);
	}
	
	public static double getCornerY(AxisAlignedBB bb, BoxCorner corner)
	{
		return getValueOfFacing(bb, corner.y);
	}
	
	public static double getCornerZ(AxisAlignedBB bb, BoxCorner corner)
	{
		return getValueOfFacing(bb, corner.z);
	}
	
	public static boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_)
    {
        return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
    }
}
