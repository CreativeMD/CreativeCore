package team.creative.creativecore.common.gui.control.simple;

import javax.annotation.Nullable;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.text.IComponentMap;

public class GuiStateButton<K> extends GuiButton {
    
    public GuiStateButton(IGuiParent parent, String name, IComponentMap<K> map) {
        this(parent, name, 0, map);
    }
    
    public GuiStateButton(IGuiParent parent, String name, int index, IComponentMap<K> map) {
        super(parent, name, null);
        setPressed(button -> {
            if (button == 1)
                previous();
            else
                next();
        });
        set(map);
        select(index);
    }
    
    public GuiStateButton(IGuiParent parent, String name, K value, IComponentMap<K> map) {
        this(parent, name, map);
        select(value);
    }
    
    @Override
    public GuiStateButtonDist<K> dist() {
        return (GuiStateButtonDist) super.dist();
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
    
    public static interface GuiStateButtonDist<K> extends GuiButtonDist {
        
        public void set(IComponentMap<K> builder);
        
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(int index);
        
        public void select(K key);
        
        public int indexOf(K key);
        
        public void next();
        
        public void previous();
    }
    
}
