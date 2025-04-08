package team.creative.creativecore.common.util.mc;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class StackUtils {
    
    public static void collect(ItemStack stack, @Nullable Predicate<ItemStack> predicate, List<ItemStack> stacks) {
        LazyOptional<IItemHandler> result = StackUtils.getStackInventory(stack);
        if (result.isPresent())
            collect(result.orElseThrow(RuntimeException::new), predicate, stacks);
        
    }
    
    public static void collect(IItemHandler inventory, @Nullable Predicate<ItemStack> predicate, List<ItemStack> stacks) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && (predicate == null || predicate.test(stack)))
                stacks.add(stack.copy());
            else {
                LazyOptional<IItemHandler> result = StackUtils.getStackInventory(stack);
                if (result.isPresent())
                    collect(result.orElseThrow(RuntimeException::new), predicate, stacks);
            }
        }
    }
    
    public static LazyOptional<IItemHandler> getStackInventory(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
    }
    
}