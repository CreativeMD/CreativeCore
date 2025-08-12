package team.creative.creativecore;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.group.Usergroup;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.util.player.PlayerSelector.PlayerSelectorGamemode;

public class CreativeCoreConfig {
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    public int maxGuiScale = 10;
    
    @CreativeConfig
    public NamedList<Usergroup> usergroups = new NamedList<>();
    
    public CreativeCoreConfig() {
        usergroups.put("creative", new Usergroup(new PlayerSelectorGamemode(GameType.CREATIVE)));
        usergroups.put("survival", new Usergroup(new PlayerSelectorGamemode(GameType.SURVIVAL)));
        usergroups.put("adventure", new Usergroup(new PlayerSelectorGamemode(GameType.ADVENTURE)));
    }
    
    public boolean is(Player player, String usergroup) {
        if (player == null)
            return false;
        Usergroup group = usergroups.get(usergroup);
        if (group != null)
            return group.is(player);
        return false;
    }
    
}
