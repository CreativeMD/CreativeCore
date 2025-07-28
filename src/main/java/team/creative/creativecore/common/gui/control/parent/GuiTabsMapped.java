package team.creative.creativecore.common.gui.control.parent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiTabsMapped<K> extends GuiTabs {
    
    private List<K> keys = new ArrayList<>();
    
    public GuiTabsMapped(IGuiParent parent, String name) {
        super(parent, name);
    }
    
    @Override
    @Deprecated
    public GuiParent createTab(Component component) {
        throw new UnsupportedOperationException();
    }
    
    public GuiParent createTab(K key, Component component) {
        keys.add(key);
        return super.createTab(component);
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
