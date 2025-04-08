package team.creative.creativecore.common.util.mc;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class StackUtils {
    
    public static void collect(ItemStack stack, @Nullable Predicate<ItemStack> predicate, List<ItemStack> stacks) {
        IItemHandler result = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (result != null)
            collect(result, predicate, stacks);
        
    }
    
    public static void collect(IItemHandler inventory, @Nullable Predicate<ItemStack> predicate, List<ItemStack> stacks) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && (predicate == null || predicate.test(stack)))
                stacks.add(stack.copy());
            else
                collect(stack, predicate, stacks);
        }
    }
    
}