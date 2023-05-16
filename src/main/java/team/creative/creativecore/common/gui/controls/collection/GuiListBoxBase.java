package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollY {
    
    protected List<GuiRow> rows = new ArrayList<>();
    protected List<T> content;
    protected int cachedWidth;
    protected int cachedHeight;
    
    public final boolean modifiable;
    
    public GuiListBoxBase(String name, boolean modifiable, List<T> entries) {
        super(name);
        this.content = entries;
        this.modifiable = modifiable;
        createItems();
    }
    
    protected void createItems() {
        for (int i = 0; i < content.size(); i++)
            createControl(i);
    }
    
    protected void createControl(int index) {
        GuiRow row = new GuiRow();
        super.add(row);
        GuiColumn content = (GuiColumn) new GuiColumn().setExpandableX();
        content.align = Align.CENTER;
        content.add(this.content.get(index));
        row.addColumn(content);
        if (modifiable) {
            GuiColumn remove = new GuiColumn(20);
            remove.align = Align.CENTER;
            remove.add(new GuiButtonRemove(index));
            row.addColumn(remove);
        }
        rows.add(row);
    }
    
    protected void removeControl(int index) {
        remove(rows.get(index));
        rows.remove(index);
        content.remove(index);
    }
    
    protected GuiChildControl addCustomControl(GuiControl control) {
        return super.add(control);
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiChildControl addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        this.cachedWidth = width;
        super.flowX(width, preferred);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        this.cachedHeight = height;
        super.flowY(width, height, preferred);
    }
    
    public void reflowInternal() {
        if (hasGui()) {
            super.flowX(cachedWidth, preferredWidth(cachedWidth));
            super.flowY(cachedWidth, cachedHeight, preferredHeight(cachedWidth, cachedHeight));
        }
    }
    
    public void removeItem(int index) {
        removeControl(index);
        
        if (modifiable)
            for (int i = 0; i < rows.size(); i++)
                ((GuiButtonRemove) rows.get(i).getCol(1).get("x")).index = i;
        reflowInternal();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void clearItems() {
        while (content.size() > 0)
            removeControl(content.size() - 1);
    }
    
    public void addAllItems(List<T> entries) {
        for (T entry : entries) {
            content.add(entry);
            createControl(content.size() - 1);
        }
        
        reflowInternal();
    }
    
    public void addItem(T entry) {
        content.add(entry);
        createControl(content.size() - 1);
        
        reflowInternal();
        
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    @Override
    public int size() {
        return content.size();
    }
    
    public T get(int index) {
        return content.get(index);
    }
    
    public class GuiButtonRemove extends GuiButton {
        
        public int index;
        
        public GuiButtonRemove(int index) {
            super("x", null);
            setDim(6, 8);
            setAlign(Align.CENTER);
            setTitle(Component.literal("x"));
            pressed = (x) -> GuiListBoxBase.this.removeItem(this.index);
            this.index = index;
        }
        
    }
    
}
