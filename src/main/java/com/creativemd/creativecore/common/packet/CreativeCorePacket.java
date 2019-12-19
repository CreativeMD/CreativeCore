package com.creativemd.creativecore.common.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.utils.stack.InfoStack;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeCorePacket {
	
	public static final HashMap<String, Class<? extends CreativeCorePacket>> packets = new HashMap<String, Class<? extends CreativeCorePacket>>();
	
	public static int maxPacketSize = 1047576;
	
	public static void registerPacket(Class<? extends CreativeCorePacket> PacketClass, String id) {
		packets.put(id, PacketClass);
	}
	
	public static Class<? extends CreativeCorePacket> getClassByID(String id) {
		return packets.get(id);
	}
	
	public static String getIDByClass(Class<? extends CreativeCorePacket> packet) {
		for (Entry<String, Class<? extends CreativeCorePacket>> entry : packets.entrySet()) {
			if (entry.getValue() == packet)
				return entry.getKey();
		}
		return "";
	}
	
	public static String getIDByClass(CreativeCorePacket packet) {
		return getIDByClass(packet.getClass());
	}
	
	public abstract void writeBytes(ByteBuf buf);
	
	public abstract void readBytes(ByteBuf buf);
	
	@SideOnly(Side.CLIENT)
	public abstract void executeClient(EntityPlayer player);
	
	public abstract void executeServer(EntityPlayer player);
	
	public static void writeString(ByteBuf buf, String input) {
		ByteBufUtils.writeUTF8String(buf, input);
	}
	
	public static String readString(ByteBuf buf) {
		return ByteBufUtils.readUTF8String(buf);
	}
	
	public static void writePos(ByteBuf buf, BlockPos pos) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}
	
	public static BlockPos readPos(ByteBuf buf) {
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public static void writeState(ByteBuf buf, IBlockState state) {
		buf.writeInt(Block.getStateId(state));
	}
	
	public static IBlockState readState(ByteBuf buf) {
		return Block.getStateById(buf.readInt());
	}
	
	public static void writeNBT(ByteBuf buf, NBTTagCompound nbt) {
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static NBTTagCompound readNBT(ByteBuf buf) {
		return ByteBufUtils.readTag(buf);
	}
	
	public static void writeVec3d(Vec3d vec, ByteBuf buf) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}
	
	public static Vec3d readVec3d(ByteBuf buf) {
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		return new Vec3d(x, y, z);
	}
	
	public static void writeInfoStack(ByteBuf buf, InfoStack info) {
		writeNBT(buf, info.writeToNBT(new NBTTagCompound()));
	}
	
	public static InfoStack readInfoStack(ByteBuf buf) {
		return InfoStack.parseNBT(readNBT(buf));
	}
	
	public void writeInfoStacks(ByteBuf buf, ArrayList<InfoStack> infos) {
		buf.writeInt(infos.size());
		for (int i = 0; i < infos.size(); i++) {
			writeInfoStack(buf, infos.get(i));
		}
	}
	
	public ArrayList<InfoStack> readInfoStacks(ByteBuf buf) {
		int count = buf.readInt();
		ArrayList<InfoStack> infos = new ArrayList<InfoStack>();
		for (int i = 0; i < count; i++) {
			InfoStack info = readInfoStack(buf);
			if (info != null)
				infos.add(info);
		}
		return infos;
	}
	
	public static void writeItemStack(ByteBuf buf, ItemStack stack) {
		ByteBufUtils.writeItemStack(buf, stack);
	}
	
	public static ItemStack readItemStack(ByteBuf buf) {
		return ByteBufUtils.readItemStack(buf);
	}
	
	public static EnumFacing readFacing(ByteBuf buf) {
		return EnumFacing.getFront(buf.readInt());
	}
	
	public static void writeFacing(ByteBuf buf, EnumFacing facing) {
		buf.writeInt(facing.getIndex());
	}
	
	public static void writePacket(ByteBuf buf, Packet<?> packet) {
		EnumConnectionState state = EnumConnectionState.getFromPacket(packet);
		buf.writeInt(state.getId());
		try {
			buf.writeInt(state.getPacketId(EnumPacketDirection.CLIENTBOUND, packet));
			packet.writePacketData(new PacketBuffer(buf));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Packet<?> readPacket(ByteBuf buf) {
		Packet<?> packet;
		EnumConnectionState state = EnumConnectionState.getById(buf.readInt());
		try {
			packet = state.getPacket(EnumPacketDirection.CLIENTBOUND, buf.readInt());
			packet.readPacketData(new PacketBuffer(buf));
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException(e);
		}
		return packet;
	}
	
	/*
	 * public static void writeDirection(ByteBuf buf, ForgeDirection direction) {
	 * buf.writeInt(RotationUtils.getIndex(direction)); }
	 * 
	 * public static ForgeDirection readDirection(ByteBuf buf) { return
	 * ForgeDirection.getOrientation(buf.readInt()); }
	 */
	
	public static void openContainerOnServer(EntityPlayerMP entityPlayerMP, Container container) {
		entityPlayerMP.getNextWindowId();
		entityPlayerMP.closeContainer();
		int windowId = entityPlayerMP.currentWindowId;
		entityPlayerMP.openContainer = container;
		entityPlayerMP.openContainer.windowId = windowId;
		entityPlayerMP.openContainer.addListener(entityPlayerMP);
	}
}
