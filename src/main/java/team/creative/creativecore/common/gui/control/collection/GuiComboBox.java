package team.creative.creativecore.common.gui.control.collection;

import javax.annotation.Nullable;

import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.util.text.IComponentMap;

public class GuiComboBox<K> extends GuiLabel {
    
    public GuiComboBox(IGuiParent parent, String name, K selected, IComponentMap<K> builder) {
        this(parent, name, builder);
        select(selected);
    }
    
    public GuiComboBox(IGuiParent parent, String name, IComponentMap<K> builder) {
        super(parent, name);
        set(builder);
    }
    
    @Override
    public GuiComboBoxDist<K> dist() {
        return (GuiComboBoxDist<K>) super.dist();
    }
    
    public boolean hasSearchbar() {
        return dist().hasSearchbar();
    }
    
    public GuiComboBox setSearchbar(boolean searchbar) {
        dist().setSearchbar(searchbar);
        return this;
    }
    
    public GuiComboBox setDirection(ExtensionDirection direction) {
        dist().setDirection(direction);
        return this;
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
    
    public int selectedIndex() {
        return dist().selectedIndex();
    }
    
    public static interface GuiComboBoxDist<K> extends GuiLabelDist {
        
        public boolean hasSearchbar();
        
        public void setSearchbar(boolean searchbar);
        
        public void setDirection(ExtensionDirection direction);
        
        public void set(IComponentMap<K> builder);
        
        @Nullable
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(int index);
        
        public void select(K key);
        
        public int indexOf(K key);
        
        public void next();
        
        public void previous();
        
        public int selectedIndex();
        
    }
    
}
