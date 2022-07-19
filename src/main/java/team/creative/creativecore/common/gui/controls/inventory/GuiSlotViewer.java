package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.item.ItemStack;

public class GuiSlotViewer extends GuiSlotBase {
    
    public ItemStack stack;
    
    public GuiSlotViewer(String name, ItemStack stack) {
        super(name);
        this.stack = stack;
    }
    
    public GuiSlotViewer(ItemStack stack) {
        super("");
        this.stack = stack;
    }
    
    @Override
    public ItemStack getStack() {
        return stack;
    }
    
    @Override
    protected ItemStack getStackToRender() {
        return stack;
    }
    
}
