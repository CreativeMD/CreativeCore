package team.creative.creativecore.client.gui.control.inventory;

import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.control.inventory.GuiSlotViewer;

public class GuiClientSlotViewer<T extends GuiSlotViewer> extends GuiClientSlotBase<T> {
    
    public GuiClientSlotViewer(T control) {
        super(control);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        return true;
    }
    
    @Override
    protected ItemStack getStackToRender() {
        return control.stack;
    }
    
}
