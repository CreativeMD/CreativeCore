package team.creative.creativecore.common.gui.controls.layout;

import team.creative.creativecore.common.gui.GuiControl;

public class GuiVBox extends GuiLayoutControl {
    
    public GuiVBox(String name, int x, int y) {
        super(name, x, y, 1, 1);
    }
    
    @Override
    public int getPreferredHeight() {
        int height = -spacing;
        for (GuiControl control : this)
            height += control.getPreferredHeight() + spacing;
        return height;
    }
    
}
