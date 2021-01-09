package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Vector3d;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;

public class ChildVecOrigin extends VecOrigin {
    
    public IVecOrigin parent;
    
    public ChildVecOrigin(IVecOrigin parent, Vector3d center) {
        super(center);
        this.parent = parent;
    }
    
    @Override
    public void onlyRotateWithoutCenter(Vector3d vec) {
        super.onlyRotateWithoutCenter(vec);
        parent.onlyRotateWithoutCenter(vec);
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
    public double translationCombined(Axis axis) {
        return parent.translationCombined(axis) + super.translationCombined(axis);
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
