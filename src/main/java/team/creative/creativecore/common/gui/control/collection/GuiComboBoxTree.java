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
        if (dist() != null)
            dist().select(selected, false);
    }
    
    public GuiComboBoxTree(IGuiParent parent, String name, NamedTree<K> data, Function<String, Component> title) {
        super(parent, name);
        if (dist() != null) {
            dist().init(title);
            dist().set(data, false);
        }
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
            dist().set(data, true);
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
            dist().select(path, key, true);
    }
    
    public void select(K key) {
        if (dist() != null)
            dist().select(key, true);
    }
    
    public static interface GuiComboBoxTreeDist<K> extends GuiLabelDist {
        
        public void init(Function<String, Component> title);
        
        public boolean hasSearchbar();
        
        public void setSearchbar(boolean searchbar);
        
        public void setDirection(ExtensionDirection direction);
        
        public void set(NamedTree<K> data, boolean notify);
        
        @Nullable
        public K selected();
        
        public K selected(K defaultValue);
        
        public void select(String path, K key, boolean notify);
        
        public void select(K key, boolean notify);
    }
}
