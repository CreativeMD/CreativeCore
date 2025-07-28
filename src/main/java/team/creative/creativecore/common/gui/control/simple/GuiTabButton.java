package team.creative.creativecore.common.gui.control.simple;

import javax.annotation.Nullable;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlFormattingStatic;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.util.text.IComponentMap;

public class GuiTabButton<K> extends GuiParent {
    
    public static final ControlFormatting BUTTON_ACTIVE = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting BUTTON_INACTIVE = new ControlFormattingStatic(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE_INACTIVE);
    
    public GuiTabButton(IGuiParent parent, String name, IComponentMap states) {
        this(parent, name, 0, states);
    }
    
    public GuiTabButton(IGuiParent parent, String name, int index, IComponentMap map) {
        super(parent, name, null);
        setFlow(GuiFlow.STACK_X);
        dist().set(map);
        dist().select(index);
    }
    
    @Override
    public GuiTabButtonDist<K> dist() {
        return (GuiTabButtonDist) super.dist();
    }
    
    public void set(IComponentMap<K> builder) {
        dist().set(builder);
    }
    
    @Nullable
    public K selected() {
        return dist().selected();
    }
    
    public K selected(K defaultValue) {
        return dist().selected(defaultValue);
    }
    
    public void select(int index) {
        dist().select(index);
    }
    
    public void select(K key) {
        dist().select(key);
    }
    
    public int indexOf(K key) {
        return dist().indexOf(key);
    }
    
    public void next() {
        dist().next();
    }
    
    public void previous() {
        dist().previous();
    }
    
    public int index() {
        return dist().index();
    }
    
    public static interface GuiTabButtonDist<K> extends GuiParentDistHandler {
        
        public void set(IComponentMap<K> builder);
        
        @Nullable
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(int index);
        
        public void select(K key);
        
        public int indexOf(K key);
        
        public void next();
        
        public void previous();
        
        public int index();
    }
    
}
