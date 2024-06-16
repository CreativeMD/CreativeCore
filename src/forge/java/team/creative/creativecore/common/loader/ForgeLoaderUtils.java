package team.creative.creativecore.common.loader;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;

public class ForgeLoaderUtils implements ILoaderUtils {
    
    @Override
    public int getLifeSpan(ItemEntity item) {
        return item.lifespan;
    }
    
    @Override
    public int fireItemPickupPre(ItemEntity item, Player player) {
        return EventHooks.fireItemPickupPre(item, player).canPickup().ordinal();
    }
    
    @Override
    public void fireItemPickupPost(ItemEntity item, Player player, ItemStack copy) {
        EventHooks.fireItemPickupPost(item, player, copy);
    }
    
}
