package team.creative.creativecore.common.util.mc;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class StackUtils {
    
    public static IItemHandler getStackInventory(ItemStack stack) {
        return stack.getCapability(Capabilities.ItemHandler.ITEM);
    }
    
}