package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GuiSlot extends GuiSlotBase {
    
    public final Slot slot;
    
    public GuiSlot(Container container, int index) {
        this("", container, index);
    }
    
    public GuiSlot(String name, Container container, int index) {
        this(name + index, new Slot(container, index, 0, 0));
    }
    
    public GuiSlot(Slot slot) {
        this("", slot);
    }
    
    public GuiSlot(String name, Slot slot) {
        super(name);
        this.slot = slot;
    }
    
    @Override
    public ItemStack getStack() {
        return slot.getItem();
    }
    
}
