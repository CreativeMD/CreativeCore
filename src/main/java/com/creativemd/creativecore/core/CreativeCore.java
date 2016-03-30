package com.creativemd.creativecore.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.packet.BlockUpdatePacket;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.PacketReciever;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore")
public class CreativeCore {
	
	@Instance(CreativeCore.modid)
	public static CreativeCore instance = new CreativeCore();
	
	public static final String modid = "creativecore";
	public static final String version = "1.4.0";
	public static final Logger logger = LogManager.getLogger(modid);
	
	public static SimpleNetworkWrapper network;
	//public static TickHandler tickHandler = new TickHandler();
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("creativemd");
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
		//NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		//EntityRegistry.registerModEntity(EntitySit.class, "Sit", 0, this, 250, 250, true);
		
		//Init Packets
		/*CreativeCorePacket.registerPacket(GuiUpdatePacket.class, "guiupdatepacket");
		CreativeCorePacket.registerPacket(GuiControlPacket.class, "guicontrolpacket");
		CreativeCorePacket.registerPacket(ContainerControlUpdatePacket.class, "containercontrolpacket");
		CreativeCorePacket.registerPacket(TEContainerPacket.class, "TEContainer");
		CreativeCorePacket.registerPacket(GuiLayerPacket.class, "guilayerpacket");
		CreativeCorePacket.registerPacket(OpenGuiPacket.class, "opengui");*/
		CreativeCorePacket.registerPacket(BlockUpdatePacket.class, "blockupdatepacket");
		
		//FMLCommonHandler.instance().bus().register(tickHandler);
		
		//StackInfo.registerDefaultLoaders();
		
		//if(Loader.isModLoaded("NotEnoughItems") && FMLCommonHandler.instance().getEffectiveSide().isClient())
			//NEIRecipeInfoHandler.load();
    }
}
