package team.creative.creativecore.common.loader;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

public class ForgeLoaderUtils implements ILoaderUtils {
    
    @Override
    public int getLifeSpan(ItemEntity item) {
        return item.lifespan;
    }
    
    @Override
    public int onItemPickup(ItemEntity item, Player player) {
        return ForgeEventFactory.onItemPickup(item, player);
    }
    
    @Override
    public void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack stack) {
        ForgeEventFactory.firePlayerItemPickupEvent(player, item, stack);
    }
    
}
