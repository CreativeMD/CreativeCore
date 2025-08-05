package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiButtonContext extends GuiButton {
    
    public GuiButtonContext(IGuiParent parent, String name, TextMapBuilder<Consumer<Integer>> map) {
        super(parent, name, map.first());
        set(map);
    }
    
    @Override
    public GuiButtonContextDist dist() {
        return (GuiButtonContextDist) super.dist();
    }
    
    public GuiButtonContext set(TextMapBuilder<Consumer<Integer>> map) {
        if (dist() != null)
            dist().set(map);
        return this;
    }
    
    public static interface GuiButtonContextDist extends GuiButtonDist {
        
        public void set(TextMapBuilder<Consumer<Integer>> map);
        
    }
    
}
