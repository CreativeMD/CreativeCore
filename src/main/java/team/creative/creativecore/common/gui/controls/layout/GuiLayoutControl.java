package team.creative.creativecore.common.gui.controls.layout;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public abstract class GuiLayoutControl extends GuiParent {
    
    protected Align align = Align.LEFT;
    protected VAlign valign = VAlign.TOP;
    protected int spacing = 2;
    
    public GuiLayoutControl(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }
    
    @Override
    public abstract void updateLayout();
    
    @Override
    public int getPreferredWidth() {
        int width = 0;
        for (GuiControl control : this)
            width = Math.max(width, control.getPreferredWidth());
        return width;
    }
    
    @Override
    public int getPreferredHeight() {
        int height = 0;
        for (GuiControl control : this)
            height = Math.max(height, control.getPreferredHeight());
        return height;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public String getNestedName() {
        if (getParent() instanceof GuiControl)
            return ((GuiControl) getParent()).getNestedName();
        return "";
    }
    
}
