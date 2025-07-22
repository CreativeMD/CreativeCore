package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;

public class GuiButton extends GuiLabel {
    
    public GuiButton(IGuiParent parent, String name, Consumer<Integer> pressed) {
        super(parent, name);
        setPressed(pressed);
    }
    
    @Override
    public GuiButtonDist dist() {
        return (GuiButtonDist) super.dist();
    }
    
    public void setPressed(Consumer<Integer> pressed) {
        dist().setPressed(pressed);
    }
    
    public GuiButton setHoverEffect(boolean active) {
        dist().setHoverEffect(active);
        return this;
    }
    
    public static interface GuiButtonDist extends GuiLabelDist {
        
        public void setPressed(Consumer<Integer> pressed);
        
        public void setHoverEffect(boolean active);
        
    }
    
}
