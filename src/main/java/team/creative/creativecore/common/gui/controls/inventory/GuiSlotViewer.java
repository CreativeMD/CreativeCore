package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.util.math.geo.Rect;

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
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        return true;
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
