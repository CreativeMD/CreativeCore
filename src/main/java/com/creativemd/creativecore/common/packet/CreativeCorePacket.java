package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.creativecore.common.utils.string.StringUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeCorePacket {
	
	public static final HashMap<String, Class<? extends CreativeCorePacket>> packets = new HashMap<String, Class<? extends CreativeCorePacket>>();
	
	public static void registerPacket(Class<? extends CreativeCorePacket> PacketClass, String id)
	{
		packets.put(id, PacketClass);
	}
	
	public static Class<? extends CreativeCorePacket> getClassByID(String id)
	{
		return packets.get(id);
	}
	
	public static String getIDByClass(Class<? extends CreativeCorePacket> packet)
	{
		for (Entry<String, Class<? extends CreativeCorePacket>> entry : packets.entrySet()) {
			if(entry.getValue() == packet)
				return entry.getKey();
		}
		return "";
	}
	
	public static String getIDByClass(CreativeCorePacket packet)
	{
		return getIDByClass(packet.getClass());
	}
	
	public abstract void writeBytes(ByteBuf buf);
	
	public abstract void readBytes(ByteBuf buf);
	
	@SideOnly(Side.CLIENT)
	public abstract void executeClient(EntityPlayer player);
	
	public abstract void executeServer(EntityPlayer player);
	
	public static void writeString(ByteBuf buf, String input)
	{
		ByteBufUtils.writeUTF8String(buf, input);
	}
	
	public static String readString(ByteBuf buf)
	{
		return ByteBufUtils.readUTF8String(buf);
	}
	
	public static void writePos(ByteBuf buf, BlockPos pos)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}
	
	public static BlockPos readPos(ByteBuf buf)
	{
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public static void writeNBT(ByteBuf buf, NBTTagCompound nbt)
	{
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static NBTTagCompound readNBT(ByteBuf buf)
	{
		return ByteBufUtils.readTag(buf);
	}
	
	public static void writeVec3(Vec3 vec, ByteBuf buf)
	{
		buf.writeDouble(vec.xCoord);
		buf.writeDouble(vec.yCoord);
		buf.writeDouble(vec.zCoord);
	}
	
	public static Vec3 readVec3(ByteBuf buf)
	{
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		return new Vec3(x, y, z);
	}
	
	public static void writeStackInfo(ByteBuf buf, StackInfo info)
	{
		writeString(buf, StringUtils.ObjectsToString(info));
	}
	
	public static StackInfo readStackInfo(ByteBuf buf)
	{
		Object[] objects = StringUtils.StringToObjects(readString(buf));
		if(objects.length == 1 && objects[0] instanceof StackInfo)
			return (StackInfo) objects[0];
		return null;
	}
	
	public void writeStackInfos(ByteBuf buf, ArrayList<StackInfo> infos) {
		buf.writeInt(infos.size());
		for (int i = 0; i < infos.size(); i++) {
			writeStackInfo(buf, infos.get(i));
		}
	}

	public ArrayList<StackInfo> readStackInfos(ByteBuf buf) {
		int count = buf.readInt();
		ArrayList<StackInfo> infos = new ArrayList<StackInfo>();
		for (int i = 0; i < count; i++) {
			StackInfo info = readStackInfo(buf);
			if(info != null)
				infos.add(info);
		}
		return infos;
	}
	
	public static void writeItemStack(ByteBuf buf, ItemStack stack)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}
	
	public static ItemStack readItemStack(ByteBuf buf)
	{
		return ByteBufUtils.readItemStack(buf);
	}
	
	/*public static void writeDirection(ByteBuf buf, ForgeDirection direction)
	{
		buf.writeInt(RotationUtils.getIndex(direction));
	}
	
	public static ForgeDirection readDirection(ByteBuf buf)
	{
		return ForgeDirection.getOrientation(buf.readInt());
	}*/
	
	/*public static void openContainerOnServer(EntityPlayerMP entityPlayerMP, Container container)
	{
		TODO Add Open Container on Server method
		entityPlayerMP.getNextWindowId();
        entityPlayerMP.closeContainer();
        int windowId = entityPlayerMP.currentWindowId;
        entityPlayerMP.openContainer = container;
        entityPlayerMP.openContainer.windowId = windowId;
        entityPlayerMP.openContainer.addCraftingToCrafters(entityPlayerMP);
	}*/
}
