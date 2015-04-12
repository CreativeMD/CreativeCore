package com.creativemd.creativecore.common.packet;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketReciever implements IMessageHandler<CreativeMessageHandler, IMessage>{
	
	@SideOnly(Side.CLIENT)
	public void executeClient(IMessage message)
	{
		if(message instanceof CreativeMessageHandler && ((CreativeMessageHandler)message).packet != null)
			((CreativeMessageHandler)message).packet.executeClient(Minecraft.getMinecraft().thePlayer);
	}
	
    @Override
    public CreativeMessageHandler onMessage(CreativeMessageHandler message, MessageContext ctx) {
    	if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
    	{
    		executeClient(message);
    	}else{
    		if(message instanceof CreativeMessageHandler && ((CreativeMessageHandler)message).packet != null)
    		{
    			((CreativeMessageHandler)message).packet.executeServer(ctx.getServerHandler().playerEntity);
    		}
    	}
        return null;
    }
}
