package team.creative.creativecore.common.loader;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ILoaderUtils {
    
    public int getLifeSpan(ItemEntity item);
    
    public int fireItemPickupPre(ItemEntity item, Player player);
    
    public void fireItemPickupPost(ItemEntity item, Player player, ItemStack copy);
    
}
