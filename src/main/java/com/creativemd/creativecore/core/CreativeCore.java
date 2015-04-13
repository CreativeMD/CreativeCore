package com.creativemd.creativecore.core;

import com.creativemd.creativecore.client.gui.GuiHandler;
import com.creativemd.creativecore.common.event.TickHandler;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.GuiPacket;
import com.creativemd.creativecore.common.packet.PacketReciever;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore")
public class CreativeCore {
	
	@Instance(CreativeCore.modid)
	public static CreativeCore instance = new CreativeCore();
	
	public static final String modid = "creativecore";
	public static final String version = "1.0";
	
	public static SimpleNetworkWrapper network;
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("CreativeMDPacket");
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		//Init Packets
		CreativeCorePacket.registerPacket(GuiPacket.class, "guipacket");
		
		FMLCommonHandler.instance().bus().register(new TickHandler());
    }
}
