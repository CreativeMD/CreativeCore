package com.creativemd.creativecore.common.tileentity;

import net.minecraft.util.ITickable;

public interface ICustomTickable extends ITickable {
	
	public boolean shouldTick();
	
}
