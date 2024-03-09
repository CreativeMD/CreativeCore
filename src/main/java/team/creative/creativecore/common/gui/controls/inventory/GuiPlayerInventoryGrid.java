package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.function.BiFunction;

public class GuiPlayerInventoryGrid extends GuiInventoryGrid {
    
    public GuiPlayerInventoryGrid(Player player) {
        super("player", player.getInventory(), 9, 4);
        setExpandableX();
    }
    
    @Override
    protected void createInventoryGrid(BiFunction<Container, Integer, Slot> slotFactory) {
        for (int i = 9; i < fixedSize; i++)
            addSlot(new GuiSlot(slotFactory.apply(container, i)));
        for (int i = 0; i < 9; i++)
            addSlot(new GuiSlot(slotFactory.apply(container, i)));
    }
}
