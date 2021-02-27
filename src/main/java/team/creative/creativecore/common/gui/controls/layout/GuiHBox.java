package team.creative.creativecore.common.gui.controls.layout;

import team.creative.creativecore.common.gui.GuiControl;

public class GuiHBox extends GuiLayoutControl {
    
    public GuiHBox(String name, int x, int y) {
        super(name, x, y, 1, 1);
    }
    
    @Override
    public int getPreferredWidth() {
        int width = -spacing;
        for (GuiControl control : this)
            width += control.getPreferredWidth() + spacing;
        return width;
    }
    
    @Override
    public void updateLayout() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setWidthLayout(int width) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getMinWidth() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void setHeightLayout(int height) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int getMinHeight() {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
