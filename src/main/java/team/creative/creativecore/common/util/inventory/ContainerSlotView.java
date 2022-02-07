package team.creative.creativecore.common.util.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ContainerSlotView {
    
    public static ContainerSlotView mainHand(Player player) {
        return new ContainerSlotView(player.getInventory(), player.getInventory().selected);
    }
    
    public static ContainerSlotView offHand(Player player) {
        return new ContainerSlotView(player.getInventory(), Inventory.SLOT_OFFHAND);
    }
    
    public final Container container;
    public final int index;
    
    public ContainerSlotView(Container container, int index) {
        this.container = container;
        this.index = index;
    }
    
    public ItemStack get() {
        return container.getItem(index);
    }
    
    public void set(ItemStack stack) {
        container.setItem(index, stack);
        changed();
    }
    
    public void changed() {
        container.setChanged();
    }
    
}
