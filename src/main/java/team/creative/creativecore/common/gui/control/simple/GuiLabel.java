package team.creative.creativecore.common.gui.control.simple;

import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;

public class GuiLabel extends GuiControl {
    
    public GuiLabel(IGuiParent parent, String name) {
        super(parent, name);
    }
    
    @Override
    public GuiLabelDist dist() {
        return (GuiLabelDist) super.dist();
    }
    
    public GuiLabel setDefaultColor(int color) {
        if (dist() != null)
            dist().setDefaultColor(color);
        return this;
    }
    
    public GuiLabel setDropShadow(boolean shadow) {
        if (dist() != null)
            dist().setDropShadow(shadow);
        return this;
    }
    
    public GuiLabel setAlign(Align align) {
        if (dist() != null)
            dist().setAlign(align);
        return this;
    }
    
    public GuiLabel setVAlign(VAlign valgin) {
        if (dist() != null)
            dist().setVAlign(valgin);
        return this;
    }
    
    public GuiLabel setTranslate(String translate) {
        return setTitle(translatable(translate));
    }
    
    public GuiLabel setTranslate(String translate, Object... params) {
        return setTitle(translatable(translate, params));
    }
    
    public GuiLabel setTitle(Component component) {
        if (dist() != null)
            dist().setTitle(component);
        if (hasGui())
            reflow();
        return this;
    }
    
    public GuiLabel setTitle(List<Component> components) {
        if (dist() != null)
            dist().setTitle(components);
        if (hasGui())
            reflow();
        return this;
    }
    
    public GuiLabel setScale(double scale) {
        if (dist() != null)
            dist().setScale(scale);
        return this;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public static interface GuiLabelDist extends GuiControlDistHandler {
        
        public void setDefaultColor(int color);
        
        public void setDropShadow(boolean shadow);
        
        public void setAlign(Align align);
        
        public void setVAlign(VAlign valgin);
        
        public void setTitle(Component component);
        
        public void setTitle(List<Component> components);
        
        public void setScale(double scale);
        
    }
    
}
