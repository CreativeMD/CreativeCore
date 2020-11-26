package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.OrientatedBoundingBox;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	
	public Vector3d center();
	
	public void setCenter(Vector3d vec);
	
	public Matrix3d rotation();
	
	public Matrix3d rotationInv();
	
	public Vector3d translation();
	
	public void tick();
	
	public IVecOrigin getParent();
	
	public default double translationCombined(Axis axis) {
		return VectorUtils.get(axis, translation());
	}
	
	public default void onlyRotateWithoutCenter(Vector3d vec) {
		rotation().transform(vec);
	}
	
	public default void transformPointToWorld(Vector3d vec) {
		vec.sub(center());
		rotation().transform(vec);
		vec.add(center());
		
		vec.add(translation());
	}
	
	public default void transformPointToFakeWorld(Vector3d vec) {
		vec.sub(translation());
		
		vec.sub(center());
		rotationInv().transform(vec);
		vec.add(center());
	}
	
	public default Vec3d transformPointToWorld(Vec3d vec) {
		Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
		transformPointToWorld(real);
		return new Vec3d(real.x, real.y, real.z);
	}
	
	public default Vec3d transformPointToFakeWorld(Vec3d vec) {
		Vector3d real = new Vector3d(vec.x, vec.y, vec.z);
		transformPointToFakeWorld(real);
		return new Vec3d(real.x, real.y, real.z);
	}
	
	public default AxisAlignedBB getAxisAlignedBox(AxisAlignedBB box) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxCorner.values().length; i++) {
			Vector3d vec = BoxCorner.values()[i].getVector(box);
			
			transformPointToWorld(vec);
			
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public default OrientatedBoundingBox getOrientatedBox(AxisAlignedBB box) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		for (int i = 0; i < BoxCorner.values().length; i++) {
			Vector3d vec = BoxCorner.values()[i].getVector(box);
			
			transformPointToFakeWorld(vec);
			
			minX = Math.min(minX, vec.x);
			minY = Math.min(minY, vec.y);
			minZ = Math.min(minZ, vec.z);
			maxX = Math.max(maxX, vec.x);
			maxY = Math.max(maxY, vec.y);
			maxZ = Math.max(maxZ, vec.z);
		}
		
		return new OrientatedBoundingBox(this, minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@SideOnly(Side.CLIENT)
	public default void setupRenderingInternal(Entity entity, float partialTicks) {
		double rotX = rotXLast() + (rotX() - rotXLast()) * partialTicks;
		double rotY = rotYLast() + (rotY() - rotYLast()) * partialTicks;
		double rotZ = rotZLast() + (rotZ() - rotZLast()) * partialTicks;
		
		double offX = offXLast() + (offX() - offXLast()) * partialTicks;
		double offY = offYLast() + (offY() - offYLast()) * partialTicks;
		double offZ = offZLast() + (offZ() - offZLast()) * partialTicks;
		
		Vector3d rotationCenter = center();
		
		GlStateManager.translate(offX, offY, offZ);
		
		GlStateManager.translate(rotationCenter.x, rotationCenter.y, rotationCenter.z);
		
		GL11.glRotated(rotX, 1, 0, 0);
		GL11.glRotated(rotY, 0, 1, 0);
		GL11.glRotated(rotZ, 0, 0, 1);
		
		GlStateManager.translate(-rotationCenter.x, -rotationCenter.y, -rotationCenter.z);
	}
	
	@SideOnly(Side.CLIENT)
	public default void setupRendering(Entity entity, float partialTicks) {
		GlStateManager.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
		setupRenderingInternal(entity, partialTicks);
	}
	
	public default boolean hasChanged() {
		return offXLast() != offX() || offYLast() != offY() || offZLast() != offZ() || rotXLast() != rotX() || rotYLast() != rotY() || rotZLast() != rotZ();
	}
	
}
