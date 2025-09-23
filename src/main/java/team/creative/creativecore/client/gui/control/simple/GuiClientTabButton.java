package team.creative.creativecore.client.gui.control.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiTabButton;
import team.creative.creativecore.common.gui.control.simple.GuiTabButton.GuiTabButtonDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.text.IComponentMap;
import team.creative.creativecore.common.util.type.list.Tuple;
import team.creative.creativecore.common.util.type.list.TupleList;

public class GuiClientTabButton<T extends GuiTabButton, K> extends GuiClientParent<T> implements GuiTabButtonDist<K> {
    
    protected TupleList<K, CompiledText> data;
    private List<GuiButton> buttons;
    private int index;
    private GuiButton selected;
    
    public GuiClientTabButton(T control) {
        super(control);
    }
    
    @Override
    public void set(IComponentMap<K> builder, boolean notify) {
        this.data = builder.build();
        
        buttons = new ArrayList<>();
        control.clear();
        int i = 0;
        for (Tuple<K, CompiledText> t : data) {
            final int bIndex = i;
            var b = new GuiButton(control, "b" + i, x -> select(bIndex, true));
            b.setFormatting(GuiTabButton.BUTTON_INACTIVE);
            ((GuiClientLabel) b.dist()).setText(t.value);
            control.add(b);
            buttons.add(b);
            i++;
        }
        
        select(index, notify);
    }
    
    @Override
    @Nullable
    public K selected() {
        if (selected != null)
            return data.get(index).key;
        return null;
    }
    
    @Override
    public K selected(K defaultValue) {
        var s = selected();
        if (s != null)
            return s;
        return defaultValue;
    }
    
    @Override
    public void select(int index, boolean notify) {
        this.index = Mth.clamp(index, 0, this.data.size() - 1);
        
        if (selected != null)
            selected.setFormatting(GuiTabButton.BUTTON_INACTIVE);
        
        if (!data.isEmpty()) {
            selected = buttons.get(index);
            selected.setFormatting(GuiTabButton.BUTTON_ACTIVE);
        } else
            selected = null;
        
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public void select(K key, boolean notify) {
        select(indexOf(key), notify);
    }
    
    @Override
    public int indexOf(K key) {
        for (int i = 0; i < data.size(); i++)
            if (Objects.equals(data.get(i).key, key))
                return i;
        return -1;
    }
    
    @Override
    public void next() {
        int index = this.index + 1;
        if (index >= data.size())
            index = 0;
        select(index, true);
    }
    
    @Override
    public void previous() {
        int index = this.index - 1;
        if (index < 0)
            index = data.size() - 1;
        select(index, true);
    }
    
    @Override
    public int index() {
        return index;
    }
    
}
