package team.creative.creativecore.common.util.math.origin;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.box.ABB;
import team.creative.creativecore.common.util.math.box.BoxCorner;
import team.creative.creativecore.common.util.math.vec.Vec3d;

public interface IOriginPose {
    
    public double translation(Axis axis);
    
    public void rotateWithoutCenter(Vec3d vec);
    
    public void transform(Vec3d vec);
    
    public void transformInverse(Vec3d vec);
    
    public default BlockPos transform(BlockPos pos) {
        Vec3d vec = new Vec3d(pos);
        transform(vec);
        return vec.toBlockPos();
    }
    
    public default BlockPos transformInverse(BlockPos pos) {
        Vec3d vec = new Vec3d(pos);
        transformInverse(vec);
        return vec.toBlockPos();
    }
    
    public default Vector3d transform(Vector3d vec) {
        Vec3d real = new Vec3d(vec);
        transform(real);
        return new Vector3d(real.x, real.y, real.z);
    }
    
    public default Vector3d transformInverse(Vector3d vec) {
        Vec3d real = new Vec3d(vec);
        transformInverse(real);
        return new Vector3d(real.x, real.y, real.z);
    }
    
    public default Vec3 transform(Vec3 vec) {
        Vec3d real = new Vec3d(vec);
        transform(real);
        return new Vec3(real.x, real.y, real.z);
    }
    
    public default Vec3 transformInverse(Vec3 vec) {
        Vec3d real = new Vec3d(vec);
        transformInverse(real);
        return new Vec3(real.x, real.y, real.z);
    }
    
    public default ABB transform(AABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transform(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB transformInverse(AABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformInverse(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB transform(ABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transform(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB transformInverse(ABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformInverse(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Vec3 setup(Matrix4fStack matrixStack, Vec3 cam);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public Vec3 setup(PoseStack matrixStack, Vec3 cam);
    
    public Matrix4f transform(double camX, double camY, double camZ);
    
    public Matrix4f transformInverse(double camX, double camY, double camZ);
    
}
