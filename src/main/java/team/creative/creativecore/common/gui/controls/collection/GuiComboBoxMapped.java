package team.creative.creativecore.common.gui.controls.collection;

import java.util.List;

import team.creative.creativecore.common.util.text.TextMapBuilder;

public class GuiComboBoxMapped<K> extends GuiComboBox {
    
    private List<K> keys;
    
    public GuiComboBoxMapped(String name, TextMapBuilder<K> lines) {
        super(name, lines);
        updateDisplay();
        this.keys = lines.keys();
    }
    
    @Override
    public GuiComboBoxMapped<K> setSearchbar(boolean searchbar) {
        return (GuiComboBoxMapped<K>) super.setSearchbar(searchbar);
    }
    
    public void setLines(TextMapBuilder<K> builder) {
        K key = getSelected();
        lines = builder.build();
        this.keys = builder.keys();
        int index = keys.indexOf(key);
        if (index < 0)
            index = 0;
        select(index);
        updateDisplay();
    }
    
    public K getSelected() {
        int index = getIndex();
        if (index < keys.size())
            return keys.get(index);
        return null;
    }
    
    public K getSelected(K defaultValue) {
        int index = getIndex();
        if (index < keys.size())
            return keys.get(index);
        return defaultValue;
    }
    
    public void select(K key) {
        int index = keys.indexOf(key);
        if (index != -1)
            select(index);
    }
    
    public int indexOf(K key) {
        return keys.indexOf(key);
    }
    
}
