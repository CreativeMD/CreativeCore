package com.creativemd.creativecore.common.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReciever implements IMessageHandler<CreativeMessageHandler, IMessage> {
	
	public static HashMap<PacketKey, PacketValue> splittedPackets = new HashMap<>();
	
	public static ArrayList<CreativeSplittedMessageHandler> packetsToSend = new ArrayList<>();
	
	@SideOnly(Side.CLIENT)
	public static HashMap<PacketKey, PacketValue> clientSplittedPackets;
	@SideOnly(Side.CLIENT)
	public static ArrayList<CreativeSplittedMessageHandler> clientPacketsToSend;
	
	static {
		if(FMLCommonHandler.instance().getSide().isClient())
			initClient();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient()
	{
		clientSplittedPackets = new HashMap<>();
		clientPacketsToSend = new ArrayList<>();
	}
	
	public static void refreshQueue(boolean isServer)
	{
		if(!isServer)
			refreshQueueClient();
		else{
			if(splittedPackets.isEmpty())
				return ;
			for (Iterator<Entry<PacketKey, PacketValue>> iterator = splittedPackets.entrySet().iterator(); iterator.hasNext();) {
				Entry<PacketKey, PacketValue> entry = iterator.next();
				if(entry.getValue().isExpired())
				{
					System.out.println("Packet parts expired " + (entry.getValue().received+1) + "/" + entry.getValue().amount);
					iterator.remove();
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void refreshQueueClient()
	{
		if(clientSplittedPackets.isEmpty())
			return ;
		for (Iterator<Entry<PacketKey, PacketValue>> iterator = clientSplittedPackets.entrySet().iterator(); iterator.hasNext();) {
			Entry<PacketKey, PacketValue> entry = iterator.next();
			if(entry.getValue().isExpired())
			{
				System.out.println("Packet parts expired " + (entry.getValue().received+1) + "/" + entry.getValue().amount);
				iterator.remove();
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void executeClient(IMessage message)
	{
		if(message instanceof CreativeMessageHandler)
		{
			CreativeMessageHandler cm = (CreativeMessageHandler)message;
			
			if(!cm.isLast)
			{
				PacketKey key = new PacketKey(CreativeCorePacket.getIDByClass(cm.packet), cm.uuid);
				if(clientSplittedPackets.containsKey(key))
					System.out.println("Something went wrong! Received another packet of the same type with the same uuid id! " + key);
				else
					clientSplittedPackets.put(key, new PacketValue(cm.content, cm.uuid, cm.packet, cm.amount));
			}else{
				if(cm.packet != null)
				{
					Minecraft.getMinecraft().addScheduledTask(new Runnable() {
						
						@Override
						public void run() {
							((CreativeMessageHandler)message).packet.executeClient(Minecraft.getMinecraft().player);
						}
					});
				}
			}
			
		}
	}
	
    @Override
    public CreativeMessageHandler onMessage(CreativeMessageHandler message, MessageContext ctx) {
    	if(ctx.side.isClient())
    	{
    		executeClient(message);
    	}else{
    		if(message instanceof CreativeMessageHandler)
    		{
    			CreativeMessageHandler cm = (CreativeMessageHandler)message;
    			
    			if(!cm.isLast)
    			{
    				PacketKey key = new PacketKey(CreativeCorePacket.getIDByClass(cm.packet), cm.uuid);
    				if(splittedPackets.containsKey(key))
    					System.out.println("Something went wrong! Received another packet of the same type with the same uuid id! " + key);
    				else
    					splittedPackets.put(key, new PacketValue(cm.content, cm.uuid, cm.packet, cm.amount));
    			}else{
    				if(cm.packet != null)
    				{
		    			ctx.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
							
							@Override
							public void run() {
								((CreativeMessageHandler)message).packet.executeServer(ctx.getServerHandler().player);
							}
						});
    				}
    			}
    		}
    	}
        return null;
    }
    
    public static class PacketKey {
    	
    	public final String packetID;
    	public final UUID uuid;
    	
    	public PacketKey(String packetID, UUID uuid) {
			this.packetID = packetID;
			this.uuid = uuid;
		}
    	
    	@Override
    	public int hashCode()
    	{
    		return uuid.hashCode();
    	}
    	
    	@Override
    	public boolean equals(Object object)
    	{
    		if(object instanceof PacketKey)
    			return ((PacketKey) object).packetID.equals(packetID) && ((PacketKey) object).uuid.equals(uuid);
    		return false;
    	}
    	
    	@Override
    	public String toString()
    	{
    		return "[id=" + packetID + ",uuid=" + uuid + "]";
    	}
    }
    
    public static class PacketValue {
    	
    	public static long timeToWait = 60000;
    	
    	public final ByteBuf buf;
    	public final UUID uuid;
    	public final CreativeCorePacket packet;
    	public final int amount;
    	public int received;
    	
    	public long lastReceivedPart = System.currentTimeMillis();
    	
    	public PacketValue(ByteBuf buf, UUID uuid, CreativeCorePacket packet, int amount) {
			this.buf = buf;
			this.packet = packet;
			this.amount = amount;
			this.uuid = uuid;
		}
    	
    	public boolean isComplete()
    	{
    		return received == amount-1;
    	}
    	
    	public boolean isExpired()
    	{
    		return System.currentTimeMillis() - lastReceivedPart > timeToWait;
    	}
    	
    	public void receivePacket(ByteBuf toRead, int index, int length) throws IllegalAccessException
    	{
    		received++;
    		if(received >= amount)
    			throw new IllegalAccessException("This packet received more parts than it should! packetID=" + CreativeCorePacket.getIDByClass(packet) + ", uuid=" + uuid);
    		
    		buf.writeBytes(toRead, index, length);
    		lastReceivedPart = System.nanoTime();
    	}
    	
    }
}
