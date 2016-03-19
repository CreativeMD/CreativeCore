package com.creativemd.creativecore.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityCreative extends TileEntity{
	
	public void getDescriptionNBT(NBTTagCompound nbt)
	{
		
	}
	
	@Override
	public Packet getDescriptionPacket()
    {
		NBTTagCompound nbt = new NBTTagCompound();
		getDescriptionNBT(nbt);
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), nbt);
    }
	
	public double getDistance(BlockPos coord)
	{
		return pos.distanceSq(coord);
	}
	
	@SideOnly(Side.CLIENT)
	public void updateRender()
	{
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	public void updateBlock()
	{
		if(!worldObj.isRemote)
		{
			IBlockState state = worldObj.getBlockState(pos);
			worldObj.notifyBlockUpdate(pos, state, state, 3); //TODO CHECK IF IT'S THE RIGHT METHOD
			//worldObj.markBlockForUpdate(pos);
			markDirty();
		}
	}
	
	public void receiveUpdatePacket(NBTTagCompound nbt)
	{
		
	}
	
}
