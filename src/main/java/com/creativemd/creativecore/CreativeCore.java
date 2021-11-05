package com.creativemd.creativecore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.client.CreativeCoreClient;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.config.event.ConfigEventHandler;
import com.creativemd.creativecore.common.config.gui.SubGuiClientSync;
import com.creativemd.creativecore.common.config.gui.SubGuiConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.sync.ConfigurationChangePacket;
import com.creativemd.creativecore.common.config.sync.ConfigurationClientPacket;
import com.creativemd.creativecore.common.config.sync.ConfigurationPacket;
import com.creativemd.creativecore.common.event.CreativeTickHandler;
import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiProgressBar;
import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.common.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.CreativeMessageHandler;
import com.creativemd.creativecore.common.packet.CreativeSplittedMessageHandler;
import com.creativemd.creativecore.common.packet.PacketReciever;
import com.creativemd.creativecore.common.packet.SplittedPacketReceiver;
import com.creativemd.creativecore.common.packet.gui.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiNBTPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.OpenGuiPacket;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.server.command.ConfigCommand;
import com.creativemd.creativecore.server.command.GuiCommand;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CreativeCore.modid, version = CreativeCore.version, name = "CreativeCore", acceptedMinecraftVersions = "",
    guiFactory = "com.creativemd.creativecore.client.CreativeCoreSettings")
public class CreativeCore {
    
    public static final String modid = "creativecore";
    public static final String version = "1.10.0";
    
    @Instance(CreativeCore.modid)
    public static CreativeCore instance = new CreativeCore();
    
    public static final Logger LOGGER = LogManager.getLogger(CreativeCore.modid);
    
    public static SimpleNetworkWrapper network;
    public static CreativeTickHandler guiTickHandler = new CreativeTickHandler();
    public static ConfigEventHandler configHandler;
    public static CreativeCoreConfig config;
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new GuiCommand());
        event.registerServerCommand(new ConfigCommand());
    }
    
    @EventHandler
    public void onBeforeServerStarting(FMLServerAboutToStartEvent event) {
        configHandler.serverStarting();
    }
    
    @SideOnly(Side.CLIENT)
    public void loadClientSide(boolean late) {
        if (late)
            CreativeCoreClient.doClientThingsLate();
        else
            CreativeCoreClient.doClientThings();
    }
    
    @EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = version;
        configHandler = new ConfigEventHandler(event.getModConfigurationDirectory(), LOGGER);
        
        config = new CreativeCoreConfig();
        CreativeConfigRegistry.ROOT.registerValue(modid, config);
        CreativeConfigRegistry.load(modid, Side.CLIENT);
        
        if (FMLCommonHandler.instance().getSide().isClient())
            loadClientSide(false);
    }
    
    @EventHandler
    public void Init(FMLInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("creativemd");
        network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.CLIENT);
        network.registerMessage(PacketReciever.class, CreativeMessageHandler.class, 0, Side.SERVER);
        network.registerMessage(SplittedPacketReceiver.class, CreativeSplittedMessageHandler.class, 1, Side.CLIENT);
        network.registerMessage(SplittedPacketReceiver.class, CreativeSplittedMessageHandler.class, 1, Side.SERVER);
        
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
                            public void onClicked(int x, int y, int button) {}
                        });
                        controls.add(box);
                    }
                    
                    @CustomEventSubscribe
                    public void clicked(GuiControlClickEvent event) {
                        if (event.source.is("bad?")) {
                            ((GuiProgressBar) get("progress")).pos += 1;
                            if (((GuiProgressBar) get("progress")).pos > ((GuiProgressBar) get("progress")).max)
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
        
        GuiHandler.registerGuiHandler("config", new CustomGuiHandler() {
            
            @Override
            public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
                return new SubContainerEmpty(player);
            }
            
            @Override
            @SideOnly(Side.CLIENT)
            public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
                return new SubGuiConfig(CreativeConfigRegistry.ROOT, Side.SERVER);
            }
            
        });
        
        GuiHandler.registerGuiHandler("clientconfig", new CustomGuiHandler() {
            
            @Override
            public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
                return new SubContainerEmpty(player);
            }
            
            @Override
            @SideOnly(Side.CLIENT)
            public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
                return new SubGuiClientSync(CreativeConfigRegistry.ROOT);
            }
            
        });
        
        // Init Packets
        CreativeCorePacket.registerPacket(GuiUpdatePacket.class);
        CreativeCorePacket.registerPacket(GuiLayerPacket.class);
        CreativeCorePacket.registerPacket(OpenGuiPacket.class);
        CreativeCorePacket.registerPacket(ContainerControlUpdatePacket.class);
        CreativeCorePacket.registerPacket(GuiNBTPacket.class);
        CreativeCorePacket.registerPacket(ConfigurationPacket.class);
        CreativeCorePacket.registerPacket(ConfigurationChangePacket.class);
        CreativeCorePacket.registerPacket(ConfigurationClientPacket.class);
        
        MinecraftForge.EVENT_BUS.register(guiTickHandler);
        
        // if(Loader.isModLoaded("NotEnoughItems") &&
        // FMLCommonHandler.instance().getEffectiveSide().isClient())
        // NEIRecipeInfoHandler.load();
        
        if (FMLCommonHandler.instance().getSide().isClient())
            loadClientSide(true);
    }
}
