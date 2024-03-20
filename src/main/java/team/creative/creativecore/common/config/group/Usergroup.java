package team.creative.creativecore.common.config.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.util.player.PlayerSelector;

public class Usergroup {
    
    @CreativeConfig
    public List<PlayerSelector> filters;
    
    public Usergroup() {
        this.filters = new ArrayList<>();
    }
    
    public Usergroup(PlayerSelector... selectors) {
        this.filters = Arrays.asList(selectors);
    }
    
    public boolean is(Player player) {
        for (PlayerSelector selector : filters)
            if (!selector.is(player))
                return false;
        return true;
    }
}
