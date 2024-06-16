package team.creative.creativecore.common.loader;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FabricLoaderUtils implements ILoaderUtils {
    
    @Override
    public int getLifeSpan(ItemEntity item) {
        return 6000;
    }
    
    @Override
    public int fireItemPickupPre(ItemEntity item, Player player) {
        return 0;
    }
    
    @Override
    public void fireItemPickupPost(ItemEntity item, Player player, ItemStack copy) {}
    
}
