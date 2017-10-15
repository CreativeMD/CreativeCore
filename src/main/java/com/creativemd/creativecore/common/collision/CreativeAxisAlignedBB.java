package com.creativemd.creativecore.common.collision;

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
    
}
