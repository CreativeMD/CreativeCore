package team.creative.creativecore.common.util.math.origin;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaterniond;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.phys.Vec3;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class ChildOriginPose extends OriginPose {
    
    private final IOriginPose parent;
    
    public ChildOriginPose(IOriginPose parent, Vec3d translation, Quaterniond rotation, Vec3d center) {
        super(translation, rotation, center);
        this.parent = parent;
    }
    
    @Override
    public double translation(Axis axis) {
        return parent.translation(axis) + super.translation(axis);
    }
    
    @Override
    public void rotateWithoutCenter(Vec3d vec) {
        super.rotateWithoutCenter(vec);
        parent.rotateWithoutCenter(vec);
    }
    
    @Override
    public void transform(Vec3d vec) {
        super.transform(vec);
        parent.transform(vec);
    }
    
    @Override
    public void transformInverse(Vec3d vec) {
        parent.transformInverse(vec);
        super.transformInverse(vec);
        
    }
    
    @Override
    public Vec3 setup(Matrix4fStack matrixStack, Vec3 cam) {
        cam = parent.setup(matrixStack, cam);
        cam = super.setup(matrixStack, cam);
        return cam;
    }
    
    @Override
    public Vec3 setup(PoseStack matrixStack, Vec3 cam) {
        cam = parent.setup(matrixStack, cam);
        cam = super.setup(matrixStack, cam);
        return cam;
    }
    
    @Override
    public Matrix4f transform(double camX, double camY, double camZ) {
        var result = parent.transform(camX, camY, camZ);
        result.mul(super.transform(camX, camY, camZ));
        return result;
    }
    
    @Override
    public Matrix4f transformInverse(double camX, double camY, double camZ) {
        var result = super.transformInverse(camX, camY, camZ);
        result.mul(parent.transformInverse(camX, camY, camZ));
        return result;
    }
    
}
