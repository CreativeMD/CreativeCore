package team.creative.creativecore.client.gui.control.collection;

import java.util.function.Function;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxFlexible;
import team.creative.creativecore.common.gui.control.collection.GuiComboBoxFlexible.GuiComboBoxFlexibleDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiClientComboBoxFlexible<T extends GuiComboBoxFlexible<K>, K> extends GuiClientComboBox<T, K> implements GuiComboBoxFlexibleDist<K> {
    
    protected K forced;
    protected Function<K, Component> function;
    
    public GuiClientComboBoxFlexible(T control) {
        super(control);
    }
    
    @Override
    public void init(Function<K, Component> function) {
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
    
    @Override
    public void forceSelect(K key) {
        int index = indexOf(key);
        if (index == -1) {
            forced = key;
            updateDisplay();
            raiseEvent(new GuiControlChangedEvent(control));
        } else
            select(index);
    }
    
    @Override
    public void select(int index) {
        super.select(index);
        if (selected() != null)
            forced = null;
    }
    
    @Override
    public K selected() {
        if (forced != null)
            return forced;
        return super.selected();
    }
}
