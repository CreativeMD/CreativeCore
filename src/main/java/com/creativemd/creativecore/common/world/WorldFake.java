package com.creativemd.creativecore.common.world;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.spongepowered.common.interfaces.world.IMixinWorld;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
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


public class WorldFake extends World {
	
	public final World parentWorld;
	
	@SideOnly(Side.CLIENT)
	public boolean shouldRender;
	
	public Vector3d axis;
	
	public double rotX;
	public double rotY;
	public double rotZ;
	
	public double offsetX;
	public double offsetY;
	public double offsetZ;
	
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

}
