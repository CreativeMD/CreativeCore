package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;

public interface IFakeWorld {
	
	public IVecOrigin getOrigin();
	
	public void setOrigin(IVecOrigin origin);
}
