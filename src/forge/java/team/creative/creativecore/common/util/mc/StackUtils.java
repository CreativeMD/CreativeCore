package team.creative.creativecore.common.util.mc;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class StackUtils {
    
    public static LazyOptional<IItemHandler> getStackInventory(ItemStack stack) {
        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }
    
}