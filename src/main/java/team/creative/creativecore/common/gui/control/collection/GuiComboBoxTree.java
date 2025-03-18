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
    protected final Function<String, Component> folderTitle;
    protected final Function<K, Component> valueTitle;
    protected NamedTree<K> data;
    private K selected;
    private boolean searchbar;
    
    public GuiComboBoxTree(String name, K selected, NamedTree<K> data, Function<String, Component> folderTitle, Function<K, Component> valueTitle) {
        this(name, data, folderTitle, valueTitle);
        select(selected);
    }
    
    public GuiComboBoxTree(String name, NamedTree<K> data, Function<String, Component> folderTitle, Function<K, Component> valueTitle) {
        super(name);
        this.folderTitle = folderTitle;
        this.valueTitle = valueTitle;
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
    
    public void select(K key) {
        this.selected = key;
        
        updateDisplay();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    protected void updateDisplay() {
        text = text.sameDimensions();
        if (selected != null) {
            text.setAlign(Align.CENTER);
            text.setText(valueTitle.apply(selected));
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
        return new GuiMenuRoot<K>(data, creator, folderTitle, valueTitle, this::select);
    }
    
    @Override
    public void looseFocus() {
        if (ex.checkShouldClose())
            ex.close();
    }
}
