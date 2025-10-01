package team.creative.creativecore.client.gui.control.collection;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox.GuiComboBoxDist;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxExtension;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.IComponentMap;
import team.creative.creativecore.common.util.type.list.TupleList;

public class GuiClientComboBox<T extends GuiComboBox<K>, K> extends GuiClientLabel<T> implements GuiComboBoxDist<K> {
    
    protected GuiExtensionCreator<GuiClientComboBox<T, K>, GuiComboBoxExtension> ex = new GuiExtensionCreator<GuiClientComboBox<T, K>, GuiComboBoxExtension>(this);
    protected TupleList<K, CompiledText> data;
    private int index;
    private K selected;
    private boolean searchbar;
    private ExtensionDirection direction = ExtensionDirection.BELOW_OR_ABOVE;
    
    public GuiClientComboBox(T control) {
        super(control);
    }
    
    @Override
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    protected void updateDisplay() {
        if (index >= 0 && index < data.size())
            text = data.get(index).value;
        else
            text = CompiledText.EMPTY;
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
    public void set(IComponentMap<K> builder, boolean notify) {
        this.data = builder.build();
        
        select(index, false);
        
        for (CompiledText text : data.values())
            text.setAlign(Align.CENTER);
        
        updateDisplay();
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
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
    public void select(int index, boolean notify) {
        this.index = Mth.clamp(index, 0, this.data.size() - 1);
        
        if (!data.isEmpty())
            selected = data.get(this.index).key;
        else
            selected = null;
        
        updateDisplay();
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public void select(K key, boolean notify) {
        select(indexOf(key), notify);
    }
    
    @Override
    public int indexOf(K key) {
        for (int i = 0; i < data.size(); i++)
            if (Objects.equals(data.get(i).key, key))
                return i;
        return -1;
    }
    
    @Override
    public void next() {
        int index = this.index + 1;
        if (index >= data.size())
            index = 0;
        select(index, true);
    }
    
    @Override
    public void previous() {
        int index = this.index - 1;
        if (index < 0)
            index = data.size() - 1;
        select(index, true);
    }
    
    @Override
    public int selectedIndex() {
        return index;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        for (CompiledText text : data.values())
            text.setDimension(width, Integer.MAX_VALUE);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        for (CompiledText text : data.values())
            text.setMaxHeight(height);
    }
    
    @Override
    public int preferredWidth(int availableWidth) {
        int width = 0;
        for (CompiledText text : data.values())
            width = Math.max(width, text.getTotalWidth() + 3); // +3 due to scroll bar width
        return width;
    }
    
    @Override
    public int preferredHeight(int width, int availableHeight) {
        int height = 0;
        for (CompiledText text : data.values())
            height = Math.max(height, text.getTotalHeight());
        return height;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        ex.toggle(this::createBox, direction);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    protected GuiComboBoxExtension createBox(GuiExtensionCreator<GuiClientComboBox<T, K>, GuiComboBoxExtension> creator) {
        var extension = new GuiComboBoxExtension(control.getParent(), control.name + "extension");
        ((GuiClientComboBoxExtension) extension.dist()).init(creator);
        return extension;
    }
    
    @Override
    public void looseFocus() {
        if (ex.checkShouldClose())
            ex.close();
    }
    
    public Iterable<CompiledText> lines() {
        return data.values();
    }
    
}
