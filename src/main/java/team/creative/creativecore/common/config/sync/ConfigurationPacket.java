package team.creative.creativecore.common.config.sync;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
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
        this.json = holder.save(false, ignoreRestart, Dist.DEDICATED_SERVER);
        this.ignoreRestart = ignoreRestart;
    }
    
    public ConfigurationPacket() {
        
    }
    
    @Override
    public void executeClient(PlayerEntity player) {
        ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
        if (holder != null)
            holder.load(true, ignoreRestart, json, Dist.DEDICATED_SERVER);
        updateGui(player);
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        
    }
    
    public static void updateGui(PlayerEntity player) {
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
