package team.creative.creativecore.common.config.group;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.util.player.PlayerSelector;

public class Usergroup {
    
    @CreativeConfig
    public List<PlayerSelector> filters = new ArrayList<>();
    
    public Usergroup() {
        
    }
    
    public Usergroup(PlayerSelector... selectors) {
        for (PlayerSelector selector : selectors)
            filters.add(selector);
    }
    
    public boolean is(ServerPlayer player) {
        for (PlayerSelector selector : filters)
            if (!selector.is(player))
                return false;
        return true;
    }
    
}
