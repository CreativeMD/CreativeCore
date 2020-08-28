package com.creativemd.creativecore.client.rendering.face;

import java.util.List;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

public class CachedFaceRenderType implements IFaceRenderType {
	
	private final boolean shouldBeRendered;
	private final boolean outside;
	private final List<VectorFan> fans;
	private final float scale;
	
	public CachedFaceRenderType(List<VectorFan> fans, float scale, boolean shouldBeRendered, boolean outside) {
		this.fans = fans;
		this.scale = scale;
		this.shouldBeRendered = shouldBeRendered;
		this.outside = outside;
	}
	
	@Override
	public boolean shouldRender() {
		return shouldBeRendered;
	}
	
	@Override
	public boolean isOutside() {
		return outside;
	}
	
	@Override
	public boolean hasCachedFans() {
		return true;
	}
	
	@Override
	public List<VectorFan> getCachedFans() {
		return fans;
	}
	
	@Override
	public float getScale() {
		return scale;
	}
}
