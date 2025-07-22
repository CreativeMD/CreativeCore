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
