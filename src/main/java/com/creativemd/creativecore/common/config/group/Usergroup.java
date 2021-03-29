package com.creativemd.creativecore.common.config.group;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;

import net.minecraft.entity.player.EntityPlayer;

public class Usergroup {
    
    @CreativeConfig
    public List<PlayerSelector> filters = new ArrayList<>();
    
    public Usergroup() {
        
    }
    
    public Usergroup(PlayerSelector... selectors) {
        for (PlayerSelector selector : selectors)
            filters.add(selector);
    }
    
    public boolean is(EntityPlayer player) {
        for (PlayerSelector selector : filters)
            if (!selector.is(player))
                return false;
        return true;
    }
    
}
