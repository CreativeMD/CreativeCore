package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Vector3d;

import net.minecraft.entity.Entity;

public class ChildVecOrigin extends VecOrigin {
	
	public IVecOrigin parent;
	
	public ChildVecOrigin(IVecOrigin parent, Vector3d center) {
		super(center);
		this.parent = parent;
	}
	
	@Override
	public void transformPointToWorld(Vector3d vec) {
		super.transformPointToWorld(vec);
		parent.transformPointToWorld(vec);
	}
	
	@Override
	public void transformPointToFakeWorld(Vector3d vec) {
		parent.transformPointToFakeWorld(vec);
		super.transformPointToFakeWorld(vec);
		
	}
	
	@Override
	public void setupRenderingInternal(Entity entity, float partialTicks) {
		parent.setupRenderingInternal(entity, partialTicks);
		super.setupRenderingInternal(entity, partialTicks);
	}
	
	@Override
	public boolean hasChanged() {
		return super.hasChanged() || parent.hasChanged();
	}
	
	@Override
	public IVecOrigin getParent() {
		return parent;
	}
}
