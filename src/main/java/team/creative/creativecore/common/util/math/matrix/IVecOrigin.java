package team.creative.creativecore.common.util.math.matrix;

import org.joml.Quaternionf;
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

public interface IVecOrigin {
    
    public double offX();
    
    public double offY();
    
    public double offZ();
    
    public double rotX();
    
    public double rotY();
    
    public double rotZ();
    
    public double offXLast();
    
    public double offYLast();
    
    public double offZLast();
    
    public double rotXLast();
    
    public double rotYLast();
    
    public double rotZLast();
    
    public boolean isRotated();
    
    public void offX(double value);
    
    public void offY(double value);
    
    public void offZ(double value);
    
    public void off(double x, double y, double z);
    
    public void rotX(double value);
    
    public void rotY(double value);
    
    public void rotZ(double value);
    
    public void rot(double x, double y, double z);
    
    public Vec3d center();
    
    public void setCenter(Vec3d vec);
    
    public Matrix3 rotation();
    
    public Matrix3 rotationInv();
    
    public Vec3d translation();
    
    public void tick();
    
    public IVecOrigin getParent();
    
    public default void set(IVecOrigin origin) {
        off(origin.offXLast(), origin.offYLast(), origin.offZLast());
        rot(origin.rotXLast(), origin.rotYLast(), origin.rotZLast());
        
        tick();
        
        off(origin.offX(), origin.offY(), origin.offZ());
        rot(origin.rotX(), origin.rotY(), origin.rotZ());
    }
    
    public default double translationCombined(Axis axis) {
        return translation().get(axis);
    }
    
    public default void onlyRotateWithoutCenter(Vec3d vec) {
        rotation().transform(vec);
    }
    
    public default BlockPos transformPointToWorld(BlockPos pos) {
        Vec3d vec = new Vec3d(pos);
        transformPointToWorld(vec);
        return vec.toBlockPos();
    }
    
    public default BlockPos transformPointToFakeWorld(BlockPos pos) {
        Vec3d vec = new Vec3d(pos);
        transformPointToFakeWorld(vec);
        return vec.toBlockPos();
    }
    
    public default void transformPointToWorld(Vec3d vec) {
        vec.sub(center());
        rotation().transform(vec);
        vec.add(center());
        
        vec.add(translation());
    }
    
    public default void transformPointToFakeWorld(Vec3d vec) {
        vec.sub(translation());
        
        vec.sub(center());
        rotationInv().transform(vec);
        vec.add(center());
    }
    
    public default Vector3d transformPointToWorld(Vector3d vec) {
        Vec3d real = new Vec3d(vec);
        transformPointToWorld(real);
        return new Vector3d(real.x, real.y, real.z);
    }
    
    public default Vector3d transformPointToFakeWorld(Vector3d vec) {
        Vec3d real = new Vec3d(vec);
        transformPointToFakeWorld(real);
        return new Vector3d(real.x, real.y, real.z);
    }
    
    public default Vec3 transformPointToWorld(Vec3 vec) {
        Vec3d real = new Vec3d(vec);
        transformPointToWorld(real);
        return new Vec3(real.x, real.y, real.z);
    }
    
    public default Vec3 transformPointToFakeWorld(Vec3 vec) {
        Vec3d real = new Vec3d(vec);
        transformPointToFakeWorld(real);
        return new Vec3(real.x, real.y, real.z);
    }
    
    public default ABB getAABB(AABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformPointToWorld(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB getOBB(AABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformPointToFakeWorld(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB getAABB(ABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformPointToWorld(vec);
            
            bb.minX = Math.min(bb.minX, vec.x);
            bb.minY = Math.min(bb.minY, vec.y);
            bb.minZ = Math.min(bb.minZ, vec.z);
            bb.maxX = Math.max(bb.maxX, vec.x);
            bb.maxY = Math.max(bb.maxY, vec.y);
            bb.maxZ = Math.max(bb.maxZ, vec.z);
        }
        
        return bb;
    }
    
    public default ABB getOBB(ABB box) {
        ABB bb = ABB.createEmptyBox();
        Vec3d vec = new Vec3d();
        for (int i = 0; i < BoxCorner.values().length; i++) {
            BoxCorner.values()[i].set(box, vec);
            
            transformPointToFakeWorld(vec);
            
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
    public default void setupRenderingInternal(PoseStack matrixStack, double camX, double camY, double camZ, float partialTicks) {
        double rotX = rotXLast() + (rotX() - rotXLast()) * partialTicks;
        double rotY = rotYLast() + (rotY() - rotYLast()) * partialTicks;
        double rotZ = rotZLast() + (rotZ() - rotZLast()) * partialTicks;
        
        double offX = offXLast() + (offX() - offXLast()) * partialTicks;
        double offY = offYLast() + (offY() - offYLast()) * partialTicks;
        double offZ = offZLast() + (offZ() - offZLast()) * partialTicks;
        
        Vec3d rotationCenter = center();
        
        matrixStack.translate(offX, offY, offZ);
        
        matrixStack.translate(rotationCenter.x - camX, rotationCenter.y - camY, rotationCenter.z - camZ);
        matrixStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), (float) Math.toRadians(rotZ)));
        matrixStack.translate(-rotationCenter.x + camX, -rotationCenter.y + camY, -rotationCenter.z + camZ);
        
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public default void setupRendering(PoseStack matrixStack, double camX, double camY, double camZ, float partialTicks) {
        setupRenderingInternal(matrixStack, camX, camY, camZ, partialTicks);
    }
    
    public default boolean hasChanged() {
        return offXLast() != offX() || offYLast() != offY() || offZLast() != offZ() || rotXLast() != rotX() || rotYLast() != rotY() || rotZLast() != rotZ();
    }
    
    public IVecOrigin copy();
    
}
