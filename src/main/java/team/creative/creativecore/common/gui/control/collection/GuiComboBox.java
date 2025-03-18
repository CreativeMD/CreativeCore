package team.creative.creativecore.common.gui.control.collection;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.IComponentMap;
import team.creative.creativecore.common.util.type.list.TupleList;

public class GuiComboBox<K> extends GuiLabel {
    
    protected GuiExtensionCreator<GuiComboBox<K>, GuiComboBoxExtension> ex = new GuiExtensionCreator<GuiComboBox<K>, GuiComboBoxExtension>(this);
    protected TupleList<K, CompiledText> data;
    private int index;
    private K selected;
    private boolean searchbar;
    
    public GuiComboBox(String name, K selected, IComponentMap<K> builder) {
        this(name, builder);
        select(selected);
    }
    
    public GuiComboBox(String name, IComponentMap<K> builder) {
        super(name);
        set(builder);
    }
    
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    public GuiComboBox setSearchbar(boolean searchbar) {
        this.searchbar = searchbar;
        return this;
    }
    
    public void set(IComponentMap<K> builder) {
        this.data = builder.build();
        
        select(index);
        
        for (CompiledText text : data.values())
            text.setAlign(Align.CENTER);
        
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
    
    public void select(int index) {
        this.index = Mth.clamp(index, 0, this.data.size() - 1);
        
        if (!data.isEmpty())
            selected = data.get(index).key;
        else
            selected = null;
        
        updateDisplay();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void select(K key) {
        select(indexOf(key));
    }
    
    public int indexOf(K key) {
        for (int i = 0; i < data.size(); i++)
            if (Objects.equals(data.get(i).key, key))
                return i;
        return -1;
    }
    
    public void next() {
        int index = this.index + 1;
        if (index >= data.size())
            index = 0;
        select(index);
    }
    
    public void previous() {
        int index = this.index - 1;
        if (index < 0)
            index = data.size() - 1;
        select(index);
    }
    
    protected void updateDisplay() {
        if (index >= 0 && index < data.size())
            text = data.get(index).value;
        else
            text = CompiledText.EMPTY;
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
    
    public int selectedIndex() {
        return index;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        ex.toggle(this::createBox, rect);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    protected GuiComboBoxExtension createBox(GuiExtensionCreator<GuiComboBox<K>, GuiComboBoxExtension> creator) {
        return new GuiComboBoxExtension(name + "extension", creator);
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
