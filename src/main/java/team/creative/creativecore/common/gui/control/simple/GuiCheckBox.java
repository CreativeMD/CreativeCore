package team.creative.creativecore.common.gui.control.simple;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiCheckBox extends GuiLabel {
    
    public GuiCheckBox(IGuiParent parent, String name, boolean value) {
        super(parent, name);
        dist().set(value);
    }
    
    @Override
    public GuiCheckBoxDist dist() {
        return (GuiCheckBoxDist) super.dist();
    }
    
    public void set(boolean value) {
        dist().set(value);
    }
    
    public boolean get() {
        return dist().get();
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
        
    }
    
}
