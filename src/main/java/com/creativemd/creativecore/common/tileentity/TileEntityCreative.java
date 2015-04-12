package com.creativemd.creativecore.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityCreative extends TileEntity{
	
	public double getDistance(ChunkCoordinates coord)
	{
		return Math.sqrt(Math.pow(xCoord+coord.posX, 2) + Math.pow(yCoord+coord.posY, 2) + Math.pow(zCoord+coord.posZ, 2));
	}
	
	@SideOnly(Side.CLIENT)
	public void updateRender()
	{
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	
	public void updateBlock()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
}
