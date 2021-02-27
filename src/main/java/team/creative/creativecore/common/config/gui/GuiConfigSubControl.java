package team.creative.creativecore.common.config.gui;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiConfigSubControl extends GuiParent {
    
    protected int initalWidth;
    protected int initalHeight;
    
    public GuiConfigSubControl(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.initalWidth = width;
        this.initalHeight = height;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public void setWidthLayout(int width) {
        setWidth(width);
    }
    
    @Override
    public int getMinWidth() {
        return 20;
    }
    
    @Override
    public int getPreferredWidth() {
        return initalWidth;
    }
    
    @Override
    public void setHeightLayout(int height) {
        setHeight(height);
    }
    
    @Override
    public int getMinHeight() {
        return 10;
    }
    
    @Override
    public int getPreferredHeight() {
        return initalHeight;
    }
}
