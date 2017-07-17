package com.creativemd.creativecore;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.command.GuiCommand;
import com.creativemd.creativecore.common.entity.EntitySit;
import com.creativemd.creativecore.common.packet.BlockUpdatePacket;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.CreativeTestPacket;
import com.creativemd.creativecore.common.packet.CreativeSplittedMessageHandler;
import com.creativemd.creativecore.common.packet.PacketReciever;
import com.creativemd.creativecore.common.packet.SplittedPacketReceiver;
import com.creativemd.creativecore.common.packet.gui.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiNBTPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.OpenGuiPacket;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.stack.InfoStack;
import com.creativemd.creativecore.core.CreativeCoreClient;
import com.creativemd.creativecore.event.CreativeTickHandler;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiProgressBar;
import com.creativemd.creativecore.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.creativecore.gui.premade.SubContainerEmpty;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore",acceptedMinecraftVersions="")
public class CreativeCore {
	
	public static final String modid = "creativecore";
	public static final String version = "1.7.4";
	
	@Instance(CreativeCore.modid)
	public static CreativeCore instance = new CreativeCore();
	
	public static final Logger logger = LogManager.getLogger(CreativeCore.modid);
	
	public static SimpleNetworkWrapper network;
	public static CreativeTickHandler guiTickHandler = new CreativeTickHandler();
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new GuiCommand());
	}
	
	@SideOnly(Side.CLIENT)
	public void loadClientSide(boolean late)
	{
		if(late)
			CreativeCoreClient.doClientThingsLate();
		else
			CreativeCoreClient.doClientThings();
	}
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
		event.getModMetadata().version = version;
		
		if(FMLCommonHandler.instance().getSide().isClient())
			loadClientSide(false);
	}
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("creativemd");
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
		network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
		network.registerMessage(SplittedPacketReceiver.class, CreativeSplittedMessageHandler.class, 1, Side.CLIENT);
		network.registerMessage(SplittedPacketReceiver.class, CreativeSplittedMessageHandler.class, 1, Side.SERVER);
		
		CreativeCorePacket.registerPacket(CreativeTestPacket.class, "CCTest");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		GuiHandler.registerGuiHandler("test-gui", new CustomGuiHandler() {
			
			@Override
			@SideOnly(Side.CLIENT)
			public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
				return new SubGui("test-gui", 200, 200) {
					
					@Override
					public void createControls() {
						GuiScrollBox box = new GuiScrollBox("box", 0, 0, 150, 150);
						box.controls.add(new GuiLabel("Test", 0, 0, 194, 20, ColorUtils.WHITE));
						box.controls.add(new GuiButton("dialog", 0, 20) {
							
							@Override
							public void onClicked(int x, int y, int button) {
								openYesNoDialog("Really?");
							}
						});
						box.controls.add(new GuiCheckBox("bad?", 0, 40, false).setCustomTooltip("Tooltip"));
						box.controls.add(new GuiTextfield("example", 0, 60, 140, 14));
						box.controls.add(new GuiProgressBar("progress", 0, 80, 120, 14, 100, 30.45));
						box.controls.add(new GuiStateButton("states", 0, 0, 100, "first entry", "second", "third"));
						box.controls.add(new GuiAvatarLabel("avatar", 0, 130, ColorUtils.WHITE, new AvatarItemStack(new ItemStack(Blocks.CRAFTING_TABLE))) {
							
							@Override
							public void onClicked(int x, int y, int button) {
							}
						});
						controls.add(box);
					}
					
					@CustomEventSubscribe
					public void clicked(GuiControlClickEvent event)
					{
						if(event.source.is("bad?"))
						{
							((GuiProgressBar) get("progress")).pos += 1;
							if(((GuiProgressBar) get("progress")).pos > ((GuiProgressBar) get("progress")).max)
								((GuiProgressBar) get("progress")).pos = 0;
						}
					}
				};
			}
			
			@Override
			public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
				return new SubContainerEmpty(player);
			}
		});
		
		EntityRegistry.registerModEntity(new ResourceLocation(modid, "sit"), EntitySit.class, "Sit", 0, this, 250, 250, true);
		
		//Init Packets
		CreativeCorePacket.registerPacket(GuiUpdatePacket.class, "guiupdatepacket");
		CreativeCorePacket.registerPacket(GuiLayerPacket.class, "guilayerpacket");
		CreativeCorePacket.registerPacket(OpenGuiPacket.class, "opengui");
		CreativeCorePacket.registerPacket(BlockUpdatePacket.class, "blockupdatepacket");
		CreativeCorePacket.registerPacket(ContainerControlUpdatePacket.class, "containercontrolpacket");
		CreativeCorePacket.registerPacket(GuiNBTPacket.class, "guinbtpacket");
		
		MinecraftForge.EVENT_BUS.register(guiTickHandler);
		
		//if(Loader.isModLoaded("NotEnoughItems") && FMLCommonHandler.instance().getEffectiveSide().isClient())
			//NEIRecipeInfoHandler.load();
		
		if(FMLCommonHandler.instance().getSide().isClient())
			loadClientSide(true);
    }
}
