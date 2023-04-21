package team.creative.creativecore.common.util.math.matrix;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.entity.Entity;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class ChildVecOrigin extends VecOrigin {
    
    public IVecOrigin parent;
    
    public ChildVecOrigin(IVecOrigin parent, Vec3d center) {
        super(center);
        this.parent = parent;
    }
    
    @Override
    public void onlyRotateWithoutCenter(Vec3d vec) {
        super.onlyRotateWithoutCenter(vec);
        parent.onlyRotateWithoutCenter(vec);
    }
    
    @Override
    public void transformPointToWorld(Vec3d vec) {
        super.transformPointToWorld(vec);
        parent.transformPointToWorld(vec);
    }
    
    @Override
    public void transformPointToFakeWorld(Vec3d vec) {
        parent.transformPointToFakeWorld(vec);
        super.transformPointToFakeWorld(vec);
        
    }
    
    @Override
    public void setupRenderingInternal(PoseStack matrixStack, double camX, double camY, double camZ, Entity entity, float partialTicks) {
        parent.setupRenderingInternal(matrixStack, camX, camY, camZ, entity, partialTicks);
        super.setupRenderingInternal(matrixStack, camX, camY, camZ, entity, partialTicks);
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
