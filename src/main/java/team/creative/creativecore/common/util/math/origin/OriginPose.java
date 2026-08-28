package team.creative.creativecore.common.util.math.origin;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public class OriginPose implements IOriginPose {
    
    private final Vec3d translation;
    private final Quaterniond rotation;
    private final Vec3d center;
    
    public OriginPose(Vec3d translation, Quaterniond rotation, Vec3d center) {
        this.translation = translation;
        this.rotation = rotation;
        this.center = center;
    }
    
    @Override
    public double translation(Axis axis) {
        return translation.get(axis);
    }
    
    @Override
    public void rotateWithoutCenter(Vec3d vec) {
        vec.transform(rotation);
    }
    
    @Override
    public void transform(Vec3d vec) {
        vec.sub(center);
        vec.transform(rotation);
        vec.add(center);
        
        vec.add(translation);
    }
    
    @Override
    public void transformInverse(Vec3d vec) {
        vec.sub(translation);
        
        vec.sub(center);
        vec.transformInverse(rotation);
        vec.add(center);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Vec3 setup(Matrix4fStack matrixStack, Vec3 cam) {
        matrixStack.translate((float) translation.x, (float) translation.y, (float) translation.z);
        
        matrixStack.translate((float) (center.x - cam.x), (float) (center.y - cam.y), (float) (center.z - cam.z));
        matrixStack.rotate(new Quaternionf(rotation));
        matrixStack.translate((float) (-center.x + cam.x), (float) (-center.y + cam.y), (float) (-center.z + cam.z));
        return cam;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Vec3 setup(PoseStack matrixStack, Vec3 cam) {
        matrixStack.translate((float) translation.x, (float) translation.y, (float) translation.z);
        
        matrixStack.translate((float) (center.x - cam.x), (float) (center.y - cam.y), (float) (center.z - cam.z));
        matrixStack.mulPose(new Quaternionf(rotation));
        matrixStack.translate((float) (-center.x + cam.x), (float) (-center.y + cam.y), (float) (-center.z + cam.z));
        return cam;
    }
    
    @Override
    public Matrix4f transform(double camX, double camY, double camZ) {
        Matrix4f matrix = new Matrix4f();
        
        matrix.translate((float) translation.x, (float) translation.y, (float) translation.z);
        
        matrix.translate((float) (center.x - camX), (float) (center.y - camY), (float) (center.z - camZ));
        matrix.rotate(new Quaternionf(rotation));
        matrix.translate((float) (-center.x + camX), (float) (-center.y + camY), (float) (-center.z + camZ));
        return matrix;
    }
    
    @Override
    public Matrix4f transformInverse(double camX, double camY, double camZ) {
        Matrix4f matrix = new Matrix4f();
        
        matrix.translate((float) (center.x - camX), (float) (center.y - camY), (float) (center.z - camZ));
        matrix.rotate(new Quaternionf(rotation).invert());
        matrix.translate((float) (-center.x + camX), (float) (-center.y + camY), (float) (-center.z + camZ));
        
        matrix.translate((float) -translation.x, (float) -translation.y, (float) -translation.z);
        
        return matrix;
    }
    
}
