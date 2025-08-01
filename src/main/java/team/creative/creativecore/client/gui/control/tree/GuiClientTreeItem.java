package team.creative.creativecore.client.gui.control.tree;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.tree.GuiTreeItem;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientTreeItem<T extends GuiTreeItem> extends GuiClientParent<T> {
    
    private ItemClickState state = null;
    
    public GuiClientTreeItem(T control) {
        super(control);
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        if (state == ItemClickState.CLICKED && !control.tree.isDragged() && !rect.inside(x, y)) {
            control.tree.startDrag(control);
            state = ItemClickState.DRAGGED;
        }
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);
        
        if (state == ItemClickState.CLICKED) {
            control.tree.select(control);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            state = null;
        } else if (state == ItemClickState.DRAGGED) {
            state = null;
            if (control.tree.endDrag())
                playSound(SoundEvents.UI_BUTTON_CLICK, 0.1F, 2F);
            
        }
        
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;
        state = ItemClickState.CLICKED;
        return true;
    }
    
    @Override
    public boolean mouseDoubleClicked(double x, double y, int button) {
        control.toggle();
        control.tree.select(control);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public boolean testForDoubleClick(double x, double y, int button) {
        return button == 0;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        if (state == ItemClickState.DRAGGED)
            return ControlFormatting.OUTLINE;
        return super.getControlFormatting();
    }
    
    private static enum ItemClickState {
        
        CLICKED,
        DRAGGED;
        
    }
    
}
