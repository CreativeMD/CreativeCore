package team.creative.creativecore.common.config.sync;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.event.ConfigEventHandler;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.creativecore.common.util.type.CheckTree;

public class ConfigurationClientPacket extends CreativePacket {
    
    public String[] path;
    public List<String> enabled;
    
    public ConfigurationClientPacket(ICreativeConfigHolder holder, CheckTree<ConfigKey> tree) {
        this.path = holder.path();
        this.enabled = buildClientFieldList(tree.root, new ArrayList<>());
    }
    
    public ConfigurationClientPacket(ICreativeConfigHolder holder) {
        this.path = holder.path();
        this.enabled = ConfigEventHandler.loadClientFieldList(holder);
    }
    
    public List<String> buildClientFieldList(CheckTree<ConfigKey>.CheckTreeEntry entry, List<String> list) {
        if (entry.isEnabled() && entry.content != null) {
            String path;
            
            if (entry.parent != null && entry.parent.content != null)
                path = String.join(".", ((ICreativeConfigHolder) entry.parent.content.get()).path()) + ".";
            else
                path = "";
            
            list.add(path + entry.content.name);
            return list;
        }
        
        if (entry.children != null)
            for (CheckTree<ConfigKey>.CheckTreeEntry child : entry.children)
                buildClientFieldList(child, list);
            
        return list;
    }
    
    public ConfigurationClientPacket() {
        
    }
    
    public ICreativeConfigHolder run() {
        ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
        if (holder != null)
            ConfigEventHandler.saveClientFieldList(holder, enabled);
        return holder;
    }
    
    @Override
    public void executeClient(Player player) {
        run();
        ConfigurationPacket.updateGui(player);
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        CreativeCore.NETWORK.sendToClientAll(new ConfigurationClientPacket(run()));
        CreativeCore.CONFIG_HANDLER.saveClientFields();
        CreativeCore.CONFIG_HANDLER.save(Dist.DEDICATED_SERVER);
    }
    
}
