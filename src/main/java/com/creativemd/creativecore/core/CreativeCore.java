package com.creativemd.creativecore.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.command.GuiCommand;
import com.creativemd.creativecore.common.packet.BlockUpdatePacket;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.PacketReciever;
import com.creativemd.creativecore.common.packet.gui.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.OpenGuiPacket;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.creativecore.event.GuiTickHandler;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.creativecore.gui.premade.SubContainerEmpty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore")
public class CreativeCore {
	
	@Instance(CreativeCore.modid)
	public static CreativeCore instance = new CreativeCore();
	
	public static final String modid = "creativecore";
	public static final String version = "1.4.0";
	public static final Logger logger = LogManager.getLogger(modid);
	
	public static SimpleNetworkWrapper network;
	public static GuiTickHandler guiTickHandler = new GuiTickHandler();
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new GuiCommand());
	}
	
	@SideOnly(Side.CLIENT)
	public void loadClientSide()
	{
		CreativeCoreClient.doClientThings();
	}
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("creativemd");
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			loadClientSide();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		GuiHandler.registerGuiHandler("test-gui", new CustomGuiHandler() {
			
			@Override
			@SideOnly(Side.CLIENT)
			public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
				return new SubGui("test-gui", 200, 200) {
					
					@Override
					public void createControls() {
						controls.add(new GuiLabel("Test", 0, 0, 194, 20, ColorUtils.WHITE));
						controls.add(new GuiButton("dialog", 0, 20) {
							
							@Override
							public void onClicked(int x, int y, int button) {
								openYesNoDialog("Really?");
							}
						});
					}
				};
			}
			
			@Override
			public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
				return new SubContainerEmpty(player);
			}
		});
		
		//EntityRegistry.registerModEntity(EntitySit.class, "Sit", 0, this, 250, 250, true);
		
		//Init Packets
		CreativeCorePacket.registerPacket(GuiUpdatePacket.class, "guiupdatepacket");
		CreativeCorePacket.registerPacket(GuiLayerPacket.class, "guilayerpacket");
		CreativeCorePacket.registerPacket(OpenGuiPacket.class, "opengui");
		CreativeCorePacket.registerPacket(BlockUpdatePacket.class, "blockupdatepacket");
		CreativeCorePacket.registerPacket(ContainerControlUpdatePacket.class, "containercontrolpacket");
		
		MinecraftForge.EVENT_BUS.register(guiTickHandler);
		
		StackInfo.registerDefaultLoaders();
		
		//if(Loader.isModLoaded("NotEnoughItems") && FMLCommonHandler.instance().getEffectiveSide().isClient())
			//NEIRecipeInfoHandler.load();
    }
}
