package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
}
