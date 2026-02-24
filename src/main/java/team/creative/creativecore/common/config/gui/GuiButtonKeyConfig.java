package team.creative.creativecore.common.config.gui;

import team.creative.creativecore.common.config.premade.KeyConfig;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiButton;

public class GuiButtonKeyConfig extends GuiButton {
    
    public GuiButtonKeyConfig(IGuiParent parent, String name, KeyConfig key) {
        super(parent, name, null);
        
        if (dist() != null)
            dist().setValue(key);
    }
    
    @Override
    public GuiButtonKeyConfigDist dist() {
        return (GuiButtonKeyConfigDist) super.dist();
    }
    
    public void setValue(KeyConfig key) {
        if (dist() != null)
            dist().setValue(key);
    }
    
    public KeyConfig getValue() {
        if (dist() != null)
            return dist().getValue();
        return null;
    }
    
    public static interface GuiButtonKeyConfigDist extends GuiButtonDist {
        
        public void setValue(KeyConfig key);
        
        public KeyConfig getValue();
    }
    
}
