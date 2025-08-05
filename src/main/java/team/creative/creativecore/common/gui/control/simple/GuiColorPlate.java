package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.type.Color;

public class GuiColorPlate extends GuiControl {
    
    public GuiColorPlate(IGuiParent parent, String name, Color color) {
        super(parent, name);
        setColor(color);
    }
    
    @Override
    public GuiColorPlateDist dist() {
        return (GuiColorPlateDist) super.dist();
    }
    
    public void setColor(Color color) {
        if (dist() != null)
            dist().setColor(color);
    }
    
    public Color getColor() {
        if (dist() != null)
            return dist().getColor();
        return Color.WHITE;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public static interface GuiColorPlateDist extends GuiControlDistHandler {
        
        public void setColor(Color color);
        
        public Color getColor();
        
    }
}