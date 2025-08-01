package team.creative.creativecore.common.gui.control.inventory;

import java.util.function.BiFunction;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiPlayerInventoryGrid extends GuiInventoryGrid {
    
    public GuiPlayerInventoryGrid(IGuiParent parent) {
        super(parent, "player", parent.getPlayer().getInventory(), 9, 4);
        setExpandableX();
    }
    
    @Override
    protected void createInventoryGrid(BiFunction<Container, Integer, Slot> slotFactory) {
        var parent = getParent();
        for (int i = 9; i < fixedSize; i++)
            addSlot(new GuiSlot(parent, slotFactory.apply(container, i)));
        for (int i = 0; i < 9; i++)
            addSlot(new GuiSlot(parent, slotFactory.apply(container, i)));
    }
    
    @Override
    public ItemStack moveInside(ItemStack toAdd, int slot) {
        if (slot < 10)
            insertClever(toAdd, 10, 36);
        else
            insertClever(toAdd, 0, 10);
        return toAdd;
    }
}
