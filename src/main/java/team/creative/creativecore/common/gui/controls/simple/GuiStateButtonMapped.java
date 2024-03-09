package team.creative.creativecore.common.gui.controls.simple;

import team.creative.creativecore.common.util.text.TextMapBuilder;

import java.util.List;

public class GuiStateButtonMapped<K> extends GuiStateButton {
    
    private List<K> keys;
    
    public GuiStateButtonMapped(String name, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
    }
    
    public GuiStateButtonMapped(String name, int index, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
        setState(index);
    }
    
    public GuiStateButtonMapped(String name, K selected, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
        select(selected);
    }
    
    public K getSelected() {
        int index = getState();
        if (index < keys.size())
            return keys.get(index);
        return null;
    }
    
    public K getSelected(K defaultValue) {
        int index = getState();
        if (index < keys.size())
            return keys.get(index);
        return defaultValue;
    }
    
    public void select(K key) {
        int index = keys.indexOf(key);
        if (index != -1)
            setState(index);
    }
}
