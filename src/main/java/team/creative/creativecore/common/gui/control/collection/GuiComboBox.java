package team.creative.creativecore.common.gui.control.collection;

import javax.annotation.Nullable;

import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.util.text.IComponentMap;

public class GuiComboBox<K> extends GuiLabel {
    
    public GuiComboBox(IGuiParent parent, String name, K selected, IComponentMap<K> builder) {
        this(parent, name, builder);
        if (dist() != null)
            dist().select(selected, false);
    }
    
    public GuiComboBox(IGuiParent parent, String name, IComponentMap<K> builder) {
        super(parent, name);
        if (dist() != null)
            dist().set(builder, false);
    }
    
    @Override
    public GuiComboBoxDist<K> dist() {
        return (GuiComboBoxDist<K>) super.dist();
    }
    
    public boolean hasSearchbar() {
        if (dist() != null)
            return dist().hasSearchbar();
        return false;
    }
    
    public GuiComboBox setSearchbar(boolean searchbar) {
        if (dist() != null)
            dist().setSearchbar(searchbar);
        return this;
    }
    
    public GuiComboBox setDirection(ExtensionDirection direction) {
        if (dist() != null)
            dist().setDirection(direction);
        return this;
    }
    
    public void set(IComponentMap<K> builder) {
        if (dist() != null)
            dist().set(builder, true);
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
    
    public void selectSilent(int index) {
        if (dist() != null)
            dist().select(index, false);
    }
    
    public void select(int index) {
        if (dist() != null)
            dist().select(index, true);
    }
    
    public void selectSilent(K key) {
        if (dist() != null)
            dist().select(key, false);
    }
    
    public void select(K key) {
        if (dist() != null)
            dist().select(key, true);
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
    
    public int selectedIndex() {
        if (dist() != null)
            return dist().selectedIndex();
        return -1;
    }
    
    public static interface GuiComboBoxDist<K> extends GuiLabelDist {
        
        public boolean hasSearchbar();
        
        public void setSearchbar(boolean searchbar);
        
        public void setDirection(ExtensionDirection direction);
        
        public void set(IComponentMap<K> builder, boolean notify);
        
        @Nullable
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(int index, boolean notify);
        
        public void select(K key, boolean notify);
        
        public int indexOf(K key);
        
        public void next();
        
        public void previous();
        
        public int selectedIndex();
        
    }
    
}
