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
        dist().init(title);
        set(data);
    }
    
    @Override
    public GuiComboBoxTreeDist<K> dist() {
        return (GuiComboBoxTreeDist<K>) super.dist();
    }
    
    public boolean hasSearchbar() {
        return dist().hasSearchbar();
    }
    
    public GuiComboBoxTree setSearchbar(boolean searchbar) {
        dist().setSearchbar(searchbar);
        return this;
    }
    
    public GuiComboBoxTree setDirection(ExtensionDirection direction) {
        dist().setDirection(direction);
        return this;
    }
    
    public void set(NamedTree<K> data) {
        dist().set(data);
    }
    
    @Nullable
    public K selected() {
        return dist().selected();
    }
    
    public K selected(K defaultValue) {
        return dist().selected(defaultValue);
    }
    
    public void select(String path, K key) {
        dist().select(path, key);
    }
    
    public void select(K key) {
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
