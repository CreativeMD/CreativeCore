package team.creative.creativecore.common.gui.control.inventory;

import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;

public abstract class GuiSlotBase extends GuiControl {
    
    public GuiSlotBase(IGuiParent parent, String name) {
        super(parent, name);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public abstract ItemStack getStack();
    
}
