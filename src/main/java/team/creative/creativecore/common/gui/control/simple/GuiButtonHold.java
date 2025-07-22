package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiButtonHold.GuiButtonHoldDist;

public class GuiButtonHold<T extends GuiButtonHoldDist> extends GuiButton {
    
    public GuiButtonHold(IGuiParent parent, String name, Consumer<Integer> pressed) {
        super(parent, name, pressed);
    }
    
    @Override
    public GuiButtonHoldDist dist() {
        return (GuiButtonHoldDist) super.dist();
    }
    
    @Override
    public void tick() {
        dist().tick();
    }
    
    public static interface GuiButtonHoldDist extends GuiButtonDist {
        
        public void tick();
        
    }
}