package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Vector3d;

public class ChildVecOrigin extends VecOrigin {
	
	public IVecOrigin parent;
	
	public ChildVecOrigin(IVecOrigin parent, Vector3d center) {
		super(center);
		this.parent = parent;
	}
	
	@Override
	public void transformPointToWorld(Vector3d vec) {
		parent.transformPointToWorld(vec);
		super.transformPointToWorld(vec);
	}
	
	@Override
	public void transformPointToFakeWorld(Vector3d vec) {
		super.transformPointToFakeWorld(vec);
		parent.transformPointToFakeWorld(vec);
	}
	
}
