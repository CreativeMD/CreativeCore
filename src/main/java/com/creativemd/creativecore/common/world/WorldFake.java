package com.creativemd.creativecore.common.world;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.spongepowered.common.interfaces.world.IMixinWorld;

import com.creativemd.creativecore.common.utils.BoxUtils;
import com.creativemd.creativecore.common.utils.IVecOrigin;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class WorldFake extends World implements IVecOrigin {
	
	public final World parentWorld;
	
	@SideOnly(Side.CLIENT)
	public boolean shouldRender;
	
	protected boolean rotated = false;
	
	public Vector3d axis;
	private Vector3d translation = new Vector3d(0, 0, 0);
	private Matrix3d rotationX = BoxUtils.createIdentityMatrix();
	private Matrix3d rotationY = BoxUtils.createIdentityMatrix();
	private Matrix3d rotationZ = BoxUtils.createIdentityMatrix();
	
	protected double rotX;
	protected double rotY;
	protected double rotZ;
	
	protected double offsetX;
	protected double offsetY;
	protected double offsetZ;
	
	public static WorldFake createFakeWorld(World world)
	{
		if(world instanceof WorldServer)
			return new WorldFakeServer((WorldServer) world);
		return new WorldFake(world);
	}

	protected WorldFake(World world) {
		super(new SaveHandlerFake(world.getWorldInfo()), world.getWorldInfo(), world.provider, new Profiler(), world.isRemote);
		chunkProvider = createChunkProvider();
		parentWorld = world;
	}
	
	@Override
	public MinecraftServer getMinecraftServer() {
		return parentWorld.getMinecraftServer();
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderFake(this, this.saveHandler.getChunkLoader(provider), provider.createChunkGenerator());
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return ((ChunkProviderFake) getChunkProvider()).chunkExists(x, z);
	}
	
	public Vector3d getRotatedVector(Vector3d vec)
	{	
		Matrix3d matrixX = new Matrix3d();
		matrixX.rotX(Math.toRadians(rotX));
		
		Matrix3d matrixY = new Matrix3d();
		matrixY.rotY(Math.toRadians(rotY));
		
		Matrix3d matrixZ = new Matrix3d();
		matrixZ.rotZ(Math.toRadians(rotZ));
		
		vec.sub(axis);
		matrixX.transform(vec);
		matrixY.transform(vec);	
		matrixZ.transform(vec);	
		vec.add(axis);
		
		vec.add(new Vector3d(offsetX, offsetY, offsetZ));
		
		return vec;
	}
	
	@Override
	public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
	
	@Override
    public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
        	Vector3d pos = getRotatedVector(new Vector3d(p_190523_2_, p_190523_4_, p_190523_6_));
            ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(p_190523_1_, false, true, pos.x, pos.y, pos.z, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
        }
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() || ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
	
	private void spawnParticle(int particleID, boolean ignoreRange, double xCood, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        for (int i = 0; i < this.eventListeners.size(); ++i)
        {
        	Vector3d pos = getRotatedVector(new Vector3d(xCood, yCoord, zCoord));
            ((IWorldEventListener)this.eventListeners.get(i)).spawnParticle(particleID, ignoreRange, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed, parameters);
        }
    }

	@Override
	public double offX() {
		return offsetX;
	}

	@Override
	public double offY() {
		return offsetY;
	}

	@Override
	public double offZ() {
		return offsetZ;
	}

	@Override
	public double rotX() {
		return rotX;
	}

	@Override
	public double rotY() {
		return rotY;
	}

	@Override
	public double rotZ() {
		return rotZ;
	}

	@Override
	public boolean isRotated() {
		return rotated;
	}
	
	protected void updateRotated()
	{
		rotated = rotX % 360 != 0 || rotY % 360 != 0 || rotZ % 360 != 0;
	}

	@Override
	public void offX(double value) {
		this.offsetX = value;
		translation.set(offsetX, offsetY, offsetZ);
	}

	@Override
	public void offY(double value) {
		this.offsetY = value;
		translation.set(offsetX, offsetY, offsetZ);
	}

	@Override
	public void offZ(double value) {
		this.offsetZ = value;
		translation.set(offsetX, offsetY, offsetZ);
	}

	@Override
	public void rotX(double value) {
		this.rotX = value;
		updateRotated();
		rotationX = BoxUtils.createRotationMatrixX(rotX);
	}

	@Override
	public void rotY(double value) {
		this.rotY = value;
		updateRotated();
		rotationY = BoxUtils.createRotationMatrixY(rotY);
	}

	@Override
	public void rotZ(double value) {
		this.rotZ = value;
		updateRotated();
		rotationZ = BoxUtils.createRotationMatrixZ(rotZ);
	}

	@Override
	public Vector3d axis() {
		return axis;
	}

	@Override
	public Matrix3d rotationX() {
		return rotationX;
	}
	
	@Override
	public Matrix3d rotationY() {
		return rotationY;
	}
	
	@Override
	public Matrix3d rotationZ() {
		return rotationZ;
	}

	@Override
	public Vector3d translation() {
		return translation;
	}

}
