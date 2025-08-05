package team.creative.creativecore.common.gui.control.collection;

import java.util.function.Function;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiComboBoxFlexible<K> extends GuiComboBox<K> {
    
    public GuiComboBoxFlexible(IGuiParent parent, String name, TextMapBuilder<K> lines, Function<K, Component> function) {
        super(parent, name, lines);
        if (dist() != null)
            dist().init(function);
    }
    
    @Override
    public GuiComboBoxFlexibleDist<K> dist() {
        return (GuiComboBoxFlexibleDist<K>) super.dist();
    }
    
    public void forceSelect(K key) {
        if (dist() != null)
            dist().forceSelect(key);
    }
    
    public static interface GuiComboBoxFlexibleDist<K> extends GuiComboBoxDist<K> {
        
        public void init(Function<K, Component> function);
        
        public void forceSelect(K key);
        
    }
    
}
