package com.creativemd.creativecore.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.entity.EntitySit;
import com.creativemd.creativecore.common.event.TickHandler;
import com.creativemd.creativecore.common.gui.GuiHandler;
import com.creativemd.creativecore.common.packet.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.GuiControlPacket;
import com.creativemd.creativecore.common.packet.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.GuiUpdatePacket;
import com.creativemd.creativecore.common.packet.PacketReciever;
import com.creativemd.creativecore.common.packet.TEContainerPacket;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.creativecore.common.utils.string.StringUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore")
public class CreativeCore {
	
	@Instance(CreativeCore.modid)
	public static CreativeCore instance = new CreativeCore();
	
	public static final String modid = "creativecore";
	public static final String version = "1.2.0";
	public static final Logger logger = LogManager.getLogger(modid);
	
	public static SimpleNetworkWrapper network;
	public static TickHandler tickHandler = new TickHandler();
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("CreativeMDPacket");
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		EntityRegistry.registerModEntity(EntitySit.class, "Sit", 0, this, 250, 250, true);
		
		//Init Packets
		CreativeCorePacket.registerPacket(GuiUpdatePacket.class, "guiupdatepacket");
		CreativeCorePacket.registerPacket(GuiControlPacket.class, "guicontrolpacket");
		CreativeCorePacket.registerPacket(ContainerControlUpdatePacket.class, "containercontrolpacket");
		CreativeCorePacket.registerPacket(TEContainerPacket.class, "TEContainer");
		CreativeCorePacket.registerPacket(GuiLayerPacket.class, "guilayerpacket");
		
		FMLCommonHandler.instance().bus().register(tickHandler);
		
		StackInfo.registerDefaultLoaders();
    }
}
