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
        if (dist() != null) {
            dist().set(map);
            dist().select(index);
        }
    }
    
    @Override
    public GuiTabButtonDist<K> dist() {
        return (GuiTabButtonDist) super.dist();
    }
    
    public void set(IComponentMap<K> builder) {
        if (dist() != null)
            dist().set(builder);
    }
    
    @Nullable
    public K selected() {
        if (dist() != null)
            return dist().selected();
        return null;
    }
    
    public K selected(K defaultValue) {
        if (dist() != null)
            return dist().selected(defaultValue);
        return defaultValue;
    }
    
    public void select(int index) {
        if (dist() != null)
            dist().select(index);
    }
    
    public void select(K key) {
        if (dist() != null)
            dist().select(key);
    }
    
    public int indexOf(K key) {
        if (dist() != null)
            return dist().indexOf(key);
        return -1;
    }
    
    public void next() {
        if (dist() != null)
            dist().next();
    }
    
    public void previous() {
        if (dist() != null)
            dist().previous();
    }
    
    public int index() {
        if (dist() != null)
            return dist().index();
        return 0;
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
