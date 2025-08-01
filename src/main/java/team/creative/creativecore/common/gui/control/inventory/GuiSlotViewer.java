package team.creative.creativecore.common.gui.control.inventory;

import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiSlotViewer extends GuiSlotBase {
    
    public ItemStack stack;
    
    public GuiSlotViewer(IGuiParent parent, String name, ItemStack stack) {
        super(parent, name);
        this.stack = stack;
    }
    
    public GuiSlotViewer(IGuiParent parent, ItemStack stack) {
        super(parent, "");
        this.stack = stack;
    }
    
    @Override
    public ItemStack getStack() {
        return stack;
    }
    
}
