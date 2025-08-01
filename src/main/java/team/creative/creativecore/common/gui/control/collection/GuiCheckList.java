package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.itr.FilterIterator;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class GuiCheckList<T> extends GuiScrollY {
    
    protected List<GuiCheckListRow> rows = new ArrayList<>();
    
    public final boolean modifiable;
    
    public Predicate<T> canBeModified = x -> true;
    
    public GuiCheckList(IGuiParent parent, String name, boolean modifiable, TextMapBuilder<T> map, Object2BooleanMap<T> selected) {
        super(parent, name);
        this.modifiable = modifiable;
        if (map != null)
            set(map, null);
    }
    
    @Override
    public GuiCheckListDist dist() {
        return (GuiCheckListDist) super.dist();
    }
    
    public void set(TextMapBuilder<T> map, Object2BooleanMap<T> selected) {
        rows.clear();
        clear();
        for (Entry<T, List<Component>> entry : map.entrySet())
            createControl(entry.getKey(), entry.getValue(), selected.getBoolean(entry.getKey()));
        dist().reflowInternal();
    }
    
    protected void createControl(T key, List<Component> components, boolean selected) {
        GuiCheckListRow row = new GuiCheckListRow(this, key, components, selected);
        super.add(row);
        rows.add(row);
    }
    
    protected void removeControl(int index) {
        remove(rows.get(index));
        rows.remove(index);
    }
    
    protected GuiControl addCustomControl(GuiControl control) {
        return super.add(control);
    }
    
    protected GuiCheckList<T> addCustom(GuiControl control) {
        super.add(control);
        return this;
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
        dist().reflowInternal();
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
    
    public T getValue(int index) {
        return rows.get(index).value;
    }
    
    public Iterable<T> selectedItems() {
        return new FunctionIterator<T>(new FilterIterator<GuiCheckListRow>(rows, x -> x.checkBox.get()).iterator(), x -> x.value);
    }
    
    public Iterable<T> allItems() {
        return new FunctionIterator<>(rows, x -> x.value);
    }
    
    public boolean checked(int index) {
        return rows.get(index).checkBox.get();
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
        
        public GuiCheckListRow(IGuiParent parent, T value, List<Component> title, boolean selected) {
            super(parent);
            this.value = value;
            GuiColumn content = (GuiColumn) new GuiColumn(this).setExpandableX();
            content.setAlign(Align.LEFT);
            content.add(checkBox = new GuiCheckBox(this, "box", selected).setTitle(title));
            addColumn(content);
            if (modifiable && canBeModified.test(value)) {
                GuiColumn remove = new GuiColumn(this, 20);
                remove.setAlign(Align.CENTER);
                removeButton = new GuiButton(this, "x", (x) -> removeItem(indexOf(value)));
                removeButton.setDim(6, 8);
                removeButton.setAlign(Align.CENTER);
                removeButton.setTitle(Component.literal("x"));
                remove.add(removeButton);
                addColumn(remove);
            } else
                removeButton = null;
        }
    }
    
    public static interface GuiCheckListDist extends GuiScrollYDist {
        
        public void reflowInternal();
        
    }
    
}
