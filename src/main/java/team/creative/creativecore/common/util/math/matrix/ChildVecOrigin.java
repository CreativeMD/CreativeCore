package team.creative.creativecore.common.util.math.matrix;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.phys.Vec3;
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
    public Vec3 setupRenderingInternal(Matrix4fStack matrixStack, Vec3 cam, float partialTicks) {
        cam = parent.setupRenderingInternal(matrixStack, cam, partialTicks);
        cam = super.setupRenderingInternal(matrixStack, cam, partialTicks);
        return cam;
    }
    
    @Override
    public Vec3 setupRenderingInternal(PoseStack matrixStack, Vec3 cam, float partialTicks) {
        cam = parent.setupRenderingInternal(matrixStack, cam, partialTicks);
        cam = super.setupRenderingInternal(matrixStack, cam, partialTicks);
        return cam;
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
    
    @Override
    protected VecOrigin createInternalCopy() {
        return new ChildVecOrigin(parent, new Vec3d(center()));
    }
    
    @Override
    public Matrix4f transform(double camX, double camY, double camZ, float partialTicks) {
        var result = parent.transform(camX, camY, camZ, partialTicks);
        result.mul(super.transform(camX, camY, camZ, partialTicks));
        return result;
    }
    
    @Override
    public Matrix4f transformInverse(double camX, double camY, double camZ, float partialTicks) {
        var result = super.transformInverse(camX, camY, camZ, partialTicks);
        result.mul(parent.transformInverse(camX, camY, camZ, partialTicks));
        return result;
    }
}
