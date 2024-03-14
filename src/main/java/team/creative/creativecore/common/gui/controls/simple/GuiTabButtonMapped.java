package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiTabButtonMapped<K> extends GuiTabButton {
    
    private List<K> keys;
    
    public GuiTabButtonMapped(String name, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
    }
    
    public GuiTabButtonMapped(String name, int index, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
        select(index);
    }
    
    public GuiTabButtonMapped(String name, K selected, TextMapBuilder<K> lines) {
        super(name, lines);
        this.keys = lines.keys();
        select(selected);
    }
    
    public K getSelected() {
        int index = index();
        if (index < keys.size())
            return keys.get(index);
        return null;
    }
    
    public K getSelected(K defaultValue) {
        int index = index();
        if (index < keys.size())
            return keys.get(index);
        return defaultValue;
    }
    
    public void select(K key) {
        int index = keys.indexOf(key);
        if (index != -1)
            select(index);
    }
}
