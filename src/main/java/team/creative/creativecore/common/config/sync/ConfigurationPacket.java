package team.creative.creativecore.common.config.sync;

import com.google.gson.JsonObject;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.network.CreativePacket;

public class ConfigurationPacket extends CreativePacket {
    
    public String[] path;
    public JsonObject json;
    public boolean ignoreRestart;
    
    public ConfigurationPacket(ICreativeConfigHolder holder, boolean ignoreRestart) {
        this.path = holder.path();
        this.json = holder.save(false, ignoreRestart, Side.SERVER);
        this.ignoreRestart = ignoreRestart;
    }
    
    public ConfigurationPacket() {
        
    }
    
    @Override
    public void executeClient(Player player) {
        ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
        if (holder != null)
            holder.load(true, ignoreRestart, json, Side.SERVER);
        updateGui(player);
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        
    }
    
    public static void updateGui(Player player) {
        if (player != null && player.containerMenu instanceof IGuiIntegratedParent) {
            
            if (((IGuiIntegratedParent) player.containerMenu).isOpen(ConfigGuiLayer.class) || ((IGuiIntegratedParent) player.containerMenu).isOpen(ClientSyncGuiLayer.class))
                for (GuiLayer layer : ((IGuiIntegratedParent) player.containerMenu).getLayers())
                    if (layer instanceof ConfigGuiLayer) {
                        ((ConfigGuiLayer) layer).ROOT = new JsonObject();
                        ((ConfigGuiLayer) layer).loadHolder(((ConfigGuiLayer) layer).holder);
                    } else if (layer instanceof ClientSyncGuiLayer) {
                        ((ClientSyncGuiLayer) layer).tree.reload();
                        ((ClientSyncGuiLayer) layer).load(((ClientSyncGuiLayer) layer).currentView);
                    }
        }
    }
    
}
