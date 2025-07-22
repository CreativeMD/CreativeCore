package team.creative.creativecore.common.gui.control.simple;

import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiShowItem extends GuiControl {
    
    public ItemStack stack = ItemStack.EMPTY;
    
    public GuiShowItem(IGuiParent parent, String name) {
        super(parent, name);
    }
    
    public GuiShowItem(IGuiParent parent, String name, ItemStack stack) {
        super(parent, name);
        this.stack = stack;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
}
