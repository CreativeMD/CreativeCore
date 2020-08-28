package com.creativemd.creativecore.client.rendering.face;

import java.util.List;

import com.creativemd.creativecore.common.utils.math.vec.VectorFan;

public enum FaceRenderType implements IFaceRenderType {
	
	INSIDE_RENDERED(true, false), INSIDE_NOT_RENDERED(false, false), OUTSIDE_RENDERED(true, true), OUTSIDE_NOT_RENDERD(false, true);
	
	private final boolean shouldBeRendered;
	private final boolean outside;
	
	FaceRenderType(boolean shouldBeRendered, boolean outside) {
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
		return false;
	}
	
	@Override
	public List<VectorFan> getCachedFans() {
		return null;
	}
	
	@Override
	public float getScale() {
		return 1;
	}
}
