package team.creative.creativecore.common.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerUtils {
    
    public static CompoundTag getPersistentData(Player player) {
        return player.getPersistentData();
    }
    
    public static double getReach(Player player) {
        double attrib = player.blockInteractionRange();
        return player.isCreative() ? attrib : attrib - 0.5;
    }
    
    public static void addOrDrop(Player player, ItemStack stack) {
        if (!stack.isEmpty() && !player.addItem(stack))
            player.drop(stack, true, false);
    }
    
    public static void addOrDrop(Player player, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++)
            addOrDrop(player, container.getItem(i));
    }
}
