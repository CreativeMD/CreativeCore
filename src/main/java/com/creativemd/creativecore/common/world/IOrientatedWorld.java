package com.creativemd.creativecore.common.world;

import javax.vecmath.Vector3d;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

import net.minecraft.world.World;

public interface IOrientatedWorld {
	
	public boolean hasParent();
	
	public World getParent();
	
	public IVecOrigin getOrigin();
	
	public void setOrigin(Vector3d center);
}
