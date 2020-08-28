package com.creativemd.creativecore.client.rendering.face;

import java.util.List;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

public interface IFaceRenderType {
	
	public boolean shouldRender();
	
	public boolean isOutside();
	
	public boolean hasCachedFans();
	
	public List<VectorFan> getCachedFans();
	
	public float getScale();
}
