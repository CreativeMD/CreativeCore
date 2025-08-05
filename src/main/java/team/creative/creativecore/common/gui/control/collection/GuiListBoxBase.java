package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollY {
    
    protected List<GuiRow> rows = new ArrayList<>();
    protected List<T> content;
    
    public final boolean modifiable;
    
    public Predicate<T> canBeModified = x -> true;
    
    public GuiListBoxBase(IGuiParent parent, String name, boolean modifiable, List<T> entries) {
        super(parent, name);
        this.content = entries;
        this.modifiable = modifiable;
        createItems();
    }
    
    @Override
    public GuiListBoxBaseDist dist() {
        return (GuiListBoxBaseDist) super.dist();
    }
    
    protected void createItems() {
        for (int i = 0; i < content.size(); i++)
            createControl(i);
    }
    
    protected void createControl(int index) {
        GuiRow row = new GuiRow(this);
        super.add(row);
        GuiColumn content = (GuiColumn) new GuiColumn(this).setExpandableX();
        content.setAlign(Align.CENTER);
        content.add(this.content.get(index));
        row.addColumn(content);
        if (modifiable && canBeModified.test(this.content.get(index))) {
            GuiColumn remove = new GuiColumn(this, 20);
            remove.setAlign(Align.CENTER);
            remove.add(new GuiButtonRemove(this, index));
            row.addColumn(remove);
        }
        rows.add(row);
    }
    
    protected void removeControl(int index) {
        remove(rows.get(index));
        rows.remove(index);
        content.remove(index);
    }
    
    public GuiControl addCustomControl(GuiControl control) {
        return super.add(control);
    }
    
    @Override
    @Deprecated
    public GuiParent add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiParent addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    public void removeItem(int index) {
        removeControl(index);
        
        if (modifiable)
            for (int i = 0; i < rows.size(); i++)
                if (canBeModified.test(this.content.get(i)))
                    ((GuiButtonRemove) rows.get(i).getCol(1).get("x")).index = i;
        if (dist() != null)
            dist().reflowInternal();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void clearItems() {
        while (!content.isEmpty())
            removeControl(content.size() - 1);
    }
    
    public void addAllItems(List<T> entries) {
        for (T entry : entries) {
            content.add(entry);
            createControl(content.size() - 1);
        }
        if (dist() != null)
            dist().reflowInternal();
    }
    
    public void addItem(T entry) {
        content.add(entry);
        createControl(content.size() - 1);
        if (dist() != null)
            dist().reflowInternal();
        
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public boolean isListEmpty() {
        return content.isEmpty();
    }
    
    public int itemSize() {
        return content.size();
    }
    
    public T getItem(int index) {
        return content.get(index);
    }
    
    public Iterable<T> items() {
        return content;
    }
    
    public class GuiButtonRemove extends GuiButton {
        
        public int index;
        
        public GuiButtonRemove(IGuiParent parent, int index) {
            super(parent, "x", null);
            setDim(6, 8);
            setAlign(Align.CENTER);
            setTitle(Component.literal("x"));
            setPressed((x) -> GuiListBoxBase.this.removeItem(this.index));
            this.index = index;
        }
        
    }
    
    public static interface GuiListBoxBaseDist extends GuiScrollYDist {
        
        public void reflowInternal();
        
    }
    
}
