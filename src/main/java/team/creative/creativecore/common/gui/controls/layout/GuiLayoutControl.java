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
    
    public GuiLayoutControl(String name, int x, int y, int width, int height, Align align, VAlign valign) {
        super(name, x, y, width, height);
        this.align = align;
        this.valign = valign;
    }
    
    @Override
    public void updateLayout() {
        updateLayout(((GuiControl) getParent()).getContentWidth(), ((GuiControl) getParent()).getContentHeight());
    }
    
    public abstract void updateLayout(int width, int height);
    
    @Override
    public int getPreferredWidth() {
        if (align != Align.LEFT)
            return Integer.MAX_VALUE;
        int width = 0;
        for (GuiControl control : this)
            width = Math.max(width, control.getPreferredWidth());
        return width;
    }
    
    @Override
    public int getPreferredHeight() {
        if (valign != VAlign.TOP)
            return Integer.MAX_VALUE;
        int height = 0;
        for (GuiControl control : this)
            height = Math.max(height, control.getPreferredHeight());
        return height;
    }
    
    @Override
    public int getMinWidth() {
        int width = 0;
        for (GuiControl control : this)
            width = Math.max(width, control.getMinWidth());
        return width;
    }
    
    @Override
    public int getMinHeight() {
        int height = 0;
        for (GuiControl control : this)
            height = Math.max(height, control.getMinHeight());
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
