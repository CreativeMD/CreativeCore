package team.creative.creativecore.common.util.inventory;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.packet.ImmediateItemStackPacket;

public class ContainerSlotView {
    
    public static final ContainerSlotView EMPTY = new ContainerSlotView(null, null, 0) {
        @Override
        public ItemStack get() {
            return ItemStack.EMPTY;
        }
        
        @Override
        public void set(ItemStack stack) {}
        
        @Override
        public void changed() {}
    };
    
    public static ContainerSlotView mainHand(Player player) {
        return new ContainerSlotView(player, player.getInventory(), player.getInventory().selected);
    }
    
    public static ContainerSlotView offHand(Player player) {
        return new ContainerSlotView(player, player.getInventory(), Inventory.SLOT_OFFHAND);
    }
    
    public final Player player;
    public final Container container;
    public final int index;
    
    public ContainerSlotView(@Nullable Player player, Container container, int index) {
        this.player = player;
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
        if (player != null && player instanceof ServerPlayer s)
            CreativeCore.NETWORK.sendToClient(new ImmediateItemStackPacket(this), s);
    }
    
}
