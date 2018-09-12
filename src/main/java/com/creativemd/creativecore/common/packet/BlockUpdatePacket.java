package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.tileentity.TileEntityCreative;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockUpdatePacket extends CreativeCorePacket {

	public BlockUpdatePacket() {

	}

	public BlockPos pos;
	public NBTTagCompound nbt;

	public BlockUpdatePacket(TileEntity te, NBTTagCompound nbt) {
		this(te.getPos(), nbt);
	}

	public BlockUpdatePacket(BlockPos pos, NBTTagCompound nbt) {
		this.pos = pos;
		this.nbt = nbt;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		writePos(buf, pos);
		writeNBT(buf, nbt);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		pos = readPos(buf);
		nbt = readNBT(buf);
	}

	@Override
	public void executeClient(EntityPlayer player) {

	}

	@Override
	public void executeServer(EntityPlayer player) {
		TileEntity te = player.world.getTileEntity(pos);
		if (te instanceof TileEntityCreative) {
			((TileEntityCreative) te).receiveUpdatePacket(nbt);
		}
	}

}
