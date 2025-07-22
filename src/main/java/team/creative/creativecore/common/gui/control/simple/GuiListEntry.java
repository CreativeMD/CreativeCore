package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.common.gui.IGuiParent;

public class GuiListEntry extends GuiLabel {
    
    public int index;
    public final Consumer<Integer> consumer;
    public final boolean selected;
    
    public GuiListEntry(IGuiParent parent, String name, int index, boolean selected, Consumer<Integer> consumer) {
        super(parent, name);
        this.index = index;
        this.consumer = consumer;
        this.selected = selected;
        this.setExpandableX();
    }
    
}
