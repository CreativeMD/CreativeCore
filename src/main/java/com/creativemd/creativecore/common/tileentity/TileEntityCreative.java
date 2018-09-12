package com.creativemd.creativecore.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityCreative extends TileEntity {

	public boolean isClientSide() {
		if (world != null)
			return world.isRemote;
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}

	public void getDescriptionNBT(NBTTagCompound nbt) {

	}

	@SideOnly(Side.CLIENT)
	public void receiveUpdatePacket(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return super.getUpdateTag();
		// return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		getDescriptionNBT(nbt);
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), nbt);
	}

	public double getDistance(BlockPos coord) {
		return Math.sqrt(pos.distanceSq(coord));
	}

	@SideOnly(Side.CLIENT)
	public void updateRender() {
		world.markBlockRangeForRenderUpdate(pos, pos);
	}

	public void updateBlock() {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
			world.markChunkDirty(getPos(), this);
			// worldObj.markBlockForUpdate(pos);
			// markDirty();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		receiveUpdatePacket(pkt.getNbtCompound());
		updateRender();
	}

}
