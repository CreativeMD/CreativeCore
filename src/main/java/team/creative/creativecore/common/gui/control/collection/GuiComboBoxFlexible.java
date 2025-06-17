package team.creative.creativecore.common.gui.control.collection;

import java.util.function.Function;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiComboBoxFlexible<K> extends GuiComboBox<K> {
    
    protected K forced;
    protected Function<K, Component> function;
    
    public GuiComboBoxFlexible(String name, TextMapBuilder<K> lines, Function<K, Component> function) {
        super(name, lines);
        this.function = function;
    }
    
    @Override
    protected void updateDisplay() {
        if (forced != null) {
            text = CompiledText.createAnySize();
            text.setText(function.apply(forced));
        } else
            super.updateDisplay();
    }
    
    public void forceSelect(K key) {
        int index = indexOf(key);
        if (index == -1) {
            forced = key;
            updateDisplay();
            raiseEvent(new GuiControlChangedEvent(this));
        } else
            select(index);
    }
    
    @Override
    public void select(int index) {
        forced = null;
        super.select(index);
    }
    
    @Override
    public K selected() {
        if (forced != null)
            return forced;
        return super.selected();
    }
}
