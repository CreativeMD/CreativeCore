package team.creative.creativecore.client.gui.control.collection;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.checkerframework.checker.units.qual.K;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.control.menu.GuiClientMenuRoot;
import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxTree;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxTree.GuiComboBoxTreeDist;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiClientComboBoxTree<T extends GuiComboBoxTree<K>> extends GuiClientLabel<T> implements GuiComboBoxTreeDist<K> {
    
    protected GuiExtensionCreator<GuiClientComboBoxTree<T>, GuiMenuRoot<K>> ex = new GuiExtensionCreator<GuiClientComboBoxTree<T>, GuiMenuRoot<K>>(this);
    protected Function<String, Component> title;
    protected NamedTree<K> data;
    private String selectedPath;
    private K selected;
    private boolean searchbar;
    private ExtensionDirection direction = ExtensionDirection.BELOW_OR_ABOVE;
    
    public GuiClientComboBoxTree(T control) {
        super(control);
    }
    
    public void init(Function<String, Component> title) {
        this.title = title;
    }
    
    @Override
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    @Override
    public void setSearchbar(boolean searchbar) {
        this.searchbar = searchbar;
    }
    
    @Override
    public void setDirection(ExtensionDirection direction) {
        this.direction = direction;
    }
    
    @Override
    public void set(NamedTree<K> data) {
        this.data = data;
        select(data.first());
        updateDisplay();
    }
    
    @Override
    @Nullable
    public K selected() {
        return selected;
    }
    
    @Override
    public K selected(K defaultValue) {
        var s = selected();
        if (s != null)
            return s;
        return defaultValue;
    }
    
    @Override
    public void select(String path, K key) {
        this.selectedPath = path;
        this.selected = key;
        
        updateDisplay();
        raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public void select(K key) {
        String path = data.findPath(key);
        if (path == null)
            select(null, null);
        else
            select(path, key);
    }
    
    protected void updateDisplay() {
        text = text.sameDimensions();
        if (selected != null) {
            text.setAlign(Align.CENTER);
            text.setText(title.apply(selectedPath));
        }
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        ex.toggle(this::createBox, direction);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    protected GuiMenuRoot<K> createBox(GuiExtensionCreator<GuiClientComboBoxTree<T>, GuiMenuRoot<K>> creator) {
        var root = new GuiMenuRoot<K>(control.getParent(), data, title, this::select);
        ((GuiClientMenuRoot<K, ?>) root.dist()).parent = creator;
        return root;
    }
    
    @Override
    public void looseFocus() {
        if (ex.checkShouldClose())
            ex.close();
    }
    
}
