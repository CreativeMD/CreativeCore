package team.creative.creativecore.common.gui.controls.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.itr.FilterIterator;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class GuiCheckList<T> extends GuiScrollY {
    
    protected List<GuiCheckListRow> rows = new ArrayList<>();
    protected int cachedWidth;
    protected int cachedHeight;
    
    public final boolean modifiable;
    
    public Predicate<T> canBeModified = x -> true;
    
    public GuiCheckList(String name, boolean modifiable, TextMapBuilder<T> map, Object2BooleanMap<T> selected) {
        super(name);
        this.modifiable = modifiable;
        if (map != null)
            set(map, null);
    }
    
    public void set(TextMapBuilder<T> map, Object2BooleanMap<T> selected) {
        rows.clear();
        clear();
        for (Entry<T, List<Component>> entry : map.entrySet())
            createControl(entry.getKey(), entry.getValue(), selected.getBoolean(entry.getKey()));
        reflowInternal();
    }
    
    protected void createControl(T key, List<Component> components, boolean selected) {
        GuiCheckListRow row = new GuiCheckListRow(key, components, selected);
        super.addControl(row);
        rows.add(row);
    }
    
    protected void removeControl(int index) {
        remove(rows.get(index));
        rows.remove(index);
    }
    
    protected GuiChildControl addCustomControl(GuiControl control) {
        return super.addControl(control);
    }
    
    protected GuiCheckList<T> addCustom(GuiControl control) {
        super.addControl(control);
        return this;
    }
    
    @Override
    @Deprecated
    public GuiChildControl addControl(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiChildControl addHoverControl(GuiControl control) {
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
        reflowInternal();
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public boolean isEmpty() {
        return rows.isEmpty();
    }
    
    @Override
    public int size() {
        return rows.size();
    }
    
    public T get(int index) {
        return rows.get(index).value;
    }
    
    public Iterable<T> selectedItems() {
        return new FunctionIterator<T>(new FilterIterator<GuiCheckListRow>(rows, x -> x.checkBox.value).iterator(), x -> x.value);
    }
    
    public Iterable<T> allItems() {
        return new FunctionIterator<>(rows, x -> x.value);
    }
    
    public boolean checked(int index) {
        return rows.get(index).checkBox.value;
    }
    
    public int indexOf(T value) {
        for (int i = 0; i < rows.size(); i++)
            if (rows.get(i).value == value)
                return i;
        return -1;
    }
    
    public class GuiCheckListRow extends GuiRow {
        
        public final T value;
        public final GuiButton removeButton;
        public final GuiCheckBox checkBox;
        
        public GuiCheckListRow(T value, List<Component> title, boolean selected) {
            this.value = value;
            GuiColumn content = (GuiColumn) new GuiColumn().setExpandableX();
            content.align = Align.LEFT;
            content.add(checkBox = new GuiCheckBox("box", selected).setTitle(title));
            addColumn(content);
            if (modifiable && canBeModified.test(value)) {
                GuiColumn remove = new GuiColumn(20);
                remove.align = Align.CENTER;
                removeButton = new GuiButton("x", (x) -> removeItem(indexOf(value)));
                removeButton.setDim(6, 8);
                removeButton.setAlign(Align.CENTER);
                removeButton.setTitle(Component.literal("x"));
                remove.add(removeButton);
                addColumn(remove);
            } else
                removeButton = null;
        }
    }
    
}
