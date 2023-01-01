package team.creative.creativecore.common.gui.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.creativecore.common.util.inventory.ContainerSlotView;

public class ImmediateItemStackPacket extends CreativePacket {
    
    public ItemStack stack;
    public int index;
    
    public ImmediateItemStackPacket(ContainerSlotView view) {
        this(view.index, view.get());
    }
    
    public ImmediateItemStackPacket(int index, ItemStack stack) {
        this.index = index;
        this.stack = stack;
    }
    
    public ImmediateItemStackPacket() {}
    
    @Override
    public void executeClient(Player player) {
        player.getInventory().setItem(index, stack);
    }
    
    @Override
    public void executeServer(ServerPlayer player) {}
    
}
