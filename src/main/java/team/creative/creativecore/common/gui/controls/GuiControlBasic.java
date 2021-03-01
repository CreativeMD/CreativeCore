package team.creative.creativecore.common.gui.controls;

import team.creative.creativecore.common.gui.GuiControl;

public abstract class GuiControlBasic extends GuiControl {
    
    protected int initalWidth;
    protected int initalHeight;
    
    public GuiControlBasic(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.initalWidth = width;
        this.initalHeight = height;
    }
    
    @Override
    public void setWidthLayout(int width) {
        setWidth(width);
    }
    
    @Override
    public int getMinWidth() {
        return 10;
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
        return 2;
    }
    
    @Override
    public int getPreferredHeight() {
        return initalHeight;
    }
    
}
