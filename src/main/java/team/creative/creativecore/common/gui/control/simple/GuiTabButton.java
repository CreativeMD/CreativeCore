package team.creative.creativecore.common.gui.control.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.util.text.IComponentMap;
import team.creative.creativecore.common.util.type.list.Tuple;
import team.creative.creativecore.common.util.type.list.TupleList;

public class GuiTabButton<K> extends GuiParent {
    
    public static final ControlFormatting BUTTON_ACTIVE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting BUTTON_INACTIVE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE_INACTIVE);
    
    protected TupleList<K, CompiledText> data;
    private List<GuiBorderlessButton> buttons;
    private int index;
    private GuiBorderlessButton<K> selected;
    
    public GuiTabButton(String name, IComponentMap states) {
        this(name, 0, states);
    }
    
    public GuiTabButton(String name, int index, IComponentMap map) {
        super(name, null);
        flow = GuiFlow.STACK_X;
        set(map);
        select(index);
    }
    
    public void set(IComponentMap<K> builder) {
        this.data = builder.build();
        
        buttons = new ArrayList<>();
        clear();
        int i = 0;
        for (Tuple<K, CompiledText> t : data) {
            final int bIndex = i;
            var b = new GuiBorderlessButton("b" + i, x -> select(bIndex), t.key);
            add(b.setText(t.value));
            buttons.add(b);
            i++;
        }
        
        select(index);
    }
    
    @Nullable
    public K selected() {
        if (selected != null)
            return selected.value;
        return null;
    }
    
    public K selected(K defaultValue) {
        var s = selected();
        if (s != null)
            return s;
        return defaultValue;
    }
    
    public void select(int index) {
        this.index = Mth.clamp(index, 0, this.data.size() - 1);
        
        if (selected != null)
            selected.active = false;
        
        if (!data.isEmpty()) {
            selected = buttons.get(index);
            selected.active = true;
        } else
            selected = null;
        
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public void select(K key) {
        select(indexOf(key));
    }
    
    public int indexOf(K key) {
        for (int i = 0; i < data.size(); i++)
            if (Objects.equals(data.get(i).key, key))
                return i;
        return -1;
    }
    
    public void next() {
        int index = this.index + 1;
        if (index >= data.size())
            index = 0;
        select(index);
    }
    
    public void previous() {
        int index = this.index - 1;
        if (index < 0)
            index = data.size() - 1;
        select(index);
    }
    
    public int index() {
        return index;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public static class GuiBorderlessButton<K> extends GuiButton {
        
        public boolean active = false;
        private final K value;
        
        public GuiBorderlessButton(String name, Consumer<Integer> pressed, K value) {
            super(name, pressed);
            this.value = value;
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            if (active)
                return BUTTON_ACTIVE;
            return BUTTON_INACTIVE;
        }
    }
    
}
