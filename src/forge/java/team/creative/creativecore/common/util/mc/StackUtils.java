package team.creative.creativecore.common.util.mc;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class StackUtils {
    
    public static void collect(ItemStack stack, @Nullable Predicate<ItemStack> predicate, List<ItemStack> stacks) {
        if (predicate.test(stack))
            stacks.add(stack);
        
        if (stack.has(DataComponents.CONTAINER)) {
            var container = stack.get(DataComponents.CONTAINER);
            for (int i = 0; i < container.getSlots(); i++) {
                collect(container.getStackInSlot(i), predicate, stacks);
            }
        }
        
    }
    
}