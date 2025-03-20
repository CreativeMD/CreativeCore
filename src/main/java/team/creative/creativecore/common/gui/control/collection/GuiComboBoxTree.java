package team.creative.creativecore.common.gui.control.collection;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.menu.GuiMenuRoot;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.type.tree.NamedTree;

public class GuiComboBoxTree<K> extends GuiLabel {
    
    protected final GuiExtensionCreator<GuiComboBoxTree<K>, GuiMenuRoot<K>> ex = new GuiExtensionCreator<GuiComboBoxTree<K>, GuiMenuRoot<K>>(this);
    protected final Function<String, Component> title;
    protected NamedTree<K> data;
    private String selectedPath;
    private K selected;
    private boolean searchbar;
    
    public GuiComboBoxTree(String name, K selected, NamedTree<K> data, Function<String, Component> title) {
        this(name, data, title);
        select(selected);
    }
    
    public GuiComboBoxTree(String name, NamedTree<K> data, Function<String, Component> title) {
        super(name);
        this.title = title;
        set(data);
    }
    
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    public GuiComboBoxTree setSearchbar(boolean searchbar) {
        this.searchbar = searchbar;
        return this;
    }
    
    public void set(NamedTree<K> data) {
        this.data = data;
        select(data.first());
        updateDisplay();
    }
    
    @Nullable
    public K selected() {
        return selected;
    }
    
    public K selected(K defaultValue) {
        var s = selected();
        if (s != null)
            return s;
        return defaultValue;
    }
    
    public void select(String path, K key) {
        this.selectedPath = path;
        this.selected = key;
        
        updateDisplay();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
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
        ex.toggle(this::createBox);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    protected GuiMenuRoot<K> createBox(GuiExtensionCreator<GuiComboBoxTree<K>, GuiMenuRoot<K>> creator) {
        return new GuiMenuRoot<K>(data, creator, title, this::select);
    }
    
    @Override
    public void looseFocus() {
        if (ex.checkShouldClose())
            ex.close();
    }
}
