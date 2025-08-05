package team.creative.creativecore.common.gui.control.simple;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiCheckBox extends GuiLabel {
    
    public GuiCheckBox(IGuiParent parent, String name, boolean value) {
        super(parent, name);
        if (dist() != null)
            dist().set(value);
    }
    
    @Override
    public GuiCheckBoxDist dist() {
        return (GuiCheckBoxDist) super.dist();
    }
    
    public void set(boolean value) {
        if (dist() != null)
            dist().set(value);
    }
    
    public boolean get() {
        if (dist() != null)
            return dist().get();
        return false;
    }
    
    public GuiCheckBox setPartial(boolean partial) {
        if (dist() != null)
            dist().setPartial(partial);
        return this;
    }
    
    public boolean getPartial() {
        if (dist() != null)
            return dist().getPartial();
        return false;
    }
    
    public GuiCheckBox consumeChanged(Consumer<Boolean> changed) {
        this.consumeChanged(changed);
        return this;
    }
    
    @Override
    public GuiCheckBox setTranslate(String translate) {
        return (GuiCheckBox) super.setTranslate(translate);
    }
    
    @Override
    public GuiCheckBox setTitle(Component component) {
        return (GuiCheckBox) super.setTitle(component);
    }
    
    @Override
    public GuiCheckBox setTitle(List<Component> components) {
        return (GuiCheckBox) super.setTitle(components);
    }
    
    public static interface GuiCheckBoxDist extends GuiLabelDist {
        
        public void consumeChanged(Consumer<Boolean> changed);
        
        public void set(boolean value);
        
        public boolean get();
        
        public void setPartial(boolean partial);
        
        public boolean getPartial();
        
    }
    
}
