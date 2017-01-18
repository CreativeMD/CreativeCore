package com.creativemd.creativecore.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReciever implements IMessageHandler<CreativeMessageHandler, IMessage>{
	
	@SideOnly(Side.CLIENT)
	public void executeClient(IMessage message)
	{
		if(message instanceof CreativeMessageHandler && ((CreativeMessageHandler)message).packet != null)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					((CreativeMessageHandler)message).packet.executeClient(Minecraft.getMinecraft().thePlayer);
				}
			});
			
		}
	}
	
    @Override
    public CreativeMessageHandler onMessage(CreativeMessageHandler message, MessageContext ctx) {
    	if(ctx.side.isClient())
    	{
    		executeClient(message);
    	}else{
    		if(message instanceof CreativeMessageHandler && ((CreativeMessageHandler)message).packet != null)
    		{
    			ctx.getServerHandler().playerEntity.getServer().addScheduledTask(new Runnable() {
					
					@Override
					public void run() {
						((CreativeMessageHandler)message).packet.executeServer(ctx.getServerHandler().playerEntity);
					}
				});
    			
    		}
    	}
        return null;
    }
}
