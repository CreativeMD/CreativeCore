package team.creative.creativecore.common.util.math.matrix;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.base.Axis;
import team.creative.creativecore.common.util.math.collision.OrientatedVoxelShape;
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
    
    public default double translationCombined(Axis axis) {
        return translation().get(axis);
    }
    
    public default void onlyRotateWithoutCenter(Vec3d vec) {
        rotation().transform(vec);
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
    
    public default AxisAlignedBB getAxisAlignedBox(AxisAlignedBB box) {
        /*double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        
        for (int i = 0; i < BoxCorner.values().length; i++) {
            Vec3d vec = BoxCorner.values()[i].getVector(box);
            
            transformPointToWorld(vec);
            
            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            minZ = Math.min(minZ, vec.z);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
            maxZ = Math.max(maxZ, vec.z);
        }
        
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);*/
        return null;
    }
    
    public default OrientatedVoxelShape getOrientatedBox(AxisAlignedBB box) {
        /*double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        
        for (int i = 0; i < BoxCorner.values().length; i++) {
            Vec3d vec = BoxCorner.values()[i].getVector(box);
            
            transformPointToFakeWorld(vec);
            
            minX = Math.min(minX, vec.x);
            minY = Math.min(minY, vec.y);
            minZ = Math.min(minZ, vec.z);
            maxX = Math.max(maxX, vec.x);
            maxY = Math.max(maxY, vec.y);
            maxZ = Math.max(maxZ, vec.z);
        }
        
        return new OrientatedVoxelShape(this, minX, minY, minZ, maxX, maxY, maxZ);*/
        return null;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void setupRenderingInternal(MatrixStack matrixStack, Entity entity, float partialTicks) {
        double rotX = rotXLast() + (rotX() - rotXLast()) * partialTicks;
        double rotY = rotYLast() + (rotY() - rotYLast()) * partialTicks;
        double rotZ = rotZLast() + (rotZ() - rotZLast()) * partialTicks;
        
        double offX = offXLast() + (offX() - offXLast()) * partialTicks;
        double offY = offYLast() + (offY() - offYLast()) * partialTicks;
        double offZ = offZLast() + (offZ() - offZLast()) * partialTicks;
        
        Vec3d rotationCenter = center();
        
        matrixStack.translate(offX, offY, offZ);
        
        matrixStack.translate(rotationCenter.x, rotationCenter.y, rotationCenter.z);
        
        GL11.glRotated(rotX, 1, 0, 0);
        GL11.glRotated(rotY, 0, 1, 0);
        GL11.glRotated(rotZ, 0, 0, 1);
        
        matrixStack.translate(-rotationCenter.x, -rotationCenter.y, -rotationCenter.z);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void setupRendering(MatrixStack matrixStack, Entity entity, float partialTicks) {
        //matrixStack.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ); TODO
        setupRenderingInternal(matrixStack, entity, partialTicks);
    }
    
    public default boolean hasChanged() {
        return offXLast() != offX() || offYLast() != offY() || offZLast() != offZ() || rotXLast() != rotX() || rotYLast() != rotY() || rotZLast() != rotZ();
    }
    
}
