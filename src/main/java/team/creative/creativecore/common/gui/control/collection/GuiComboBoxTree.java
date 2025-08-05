package team.creative.creativecore.common.gui.control.collection;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiComboBoxTree<K> extends GuiLabel {
    
    public GuiComboBoxTree(IGuiParent parent, String name, K selected, NamedTree<K> data, Function<String, Component> title) {
        this(parent, name, data, title);
        select(selected);
    }
    
    public GuiComboBoxTree(IGuiParent parent, String name, NamedTree<K> data, Function<String, Component> title) {
        super(parent, name);
        if (dist() != null)
            dist().init(title);
        set(data);
    }
    
    @Override
    public GuiComboBoxTreeDist<K> dist() {
        return (GuiComboBoxTreeDist<K>) super.dist();
    }
    
    public boolean hasSearchbar() {
        if (dist() != null)
            return dist().hasSearchbar();
        return false;
    }
    
    public GuiComboBoxTree setSearchbar(boolean searchbar) {
        if (dist() != null)
            dist().setSearchbar(searchbar);
        return this;
    }
    
    public GuiComboBoxTree setDirection(ExtensionDirection direction) {
        if (dist() != null)
            dist().setDirection(direction);
        return this;
    }
    
    public void set(NamedTree<K> data) {
        if (dist() != null)
            dist().set(data);
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
    
    public void select(String path, K key) {
        if (dist() != null)
            dist().select(path, key);
    }
    
    public void select(K key) {
        if (dist() != null)
            dist().select(key);
    }
    
    public static interface GuiComboBoxTreeDist<K> extends GuiLabelDist {
        
        public void init(Function<String, Component> title);
        
        public boolean hasSearchbar();
        
        public void setSearchbar(boolean searchbar);
        
        public void setDirection(ExtensionDirection direction);
        
        public void set(NamedTree<K> data);
        
        @Nullable
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(String path, K key);
        
        public void select(K key);
    }
}
