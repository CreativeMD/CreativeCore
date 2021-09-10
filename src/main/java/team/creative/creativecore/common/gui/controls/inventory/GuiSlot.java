package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class GuiSlot extends GuiSlotBase {
    
    public Container container;
    public int index;
    
    public GuiSlot(Container container, int index) {
        this("", container, index);
    }
    
    public GuiSlot(String name, Container container, int index) {
        super(name + index);
        this.container = container;
        this.index = index;
    }
    
    @Override
    public ItemStack getStack() {
        return container.getItem(index);
    }
    
}
