package team.creative.creativecore.common.loader;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ILoaderUtils {
    
    public int getLifeSpan(ItemEntity item);
    
    public int onItemPickup(ItemEntity item, Player player);
    
    public void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack stack);
    
}
