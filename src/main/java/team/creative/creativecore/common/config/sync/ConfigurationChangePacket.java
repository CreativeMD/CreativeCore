package team.creative.creativecore.common.config.sync;

import com.google.gson.JsonObject;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.network.CreativePacket;

public class ConfigurationChangePacket extends CreativePacket {
    
    public String[] path;
    public JsonObject json;
    
    public ConfigurationChangePacket(ICreativeConfigHolder holder, JsonObject json) {
        this.path = holder.path();
        this.json = json;
    }
    
    public ConfigurationChangePacket() {
        
    }
    
    @Override
    public void executeClient(Player player) {
        
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        if (player.hasPermissions(2)) {
            CreativeConfigRegistry.ROOT.followPath(path).load(player.registryAccess(), false, true, json, Side.SERVER);
            CreativeCore.CONFIG_HANDLER.save(player.registryAccess(), Side.SERVER);
            CreativeCore.CONFIG_HANDLER.syncAll(player.getServer());
        } else
            CreativeCore.CONFIG_HANDLER.syncAll(player);
    }
    
}
