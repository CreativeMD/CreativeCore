package com.creativemd.creativecore.common.gui.controls.gui.timeline;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;

public class KeyControl<T> extends GuiControl implements Comparable<KeyControl> {
    
    public TimelineChannel channel;
    public boolean modifiable = true;
    public int tick;
    public boolean selected = false;
    public T value;
    
    public KeyControl(TimelineChannel channel, int index, int tick, T value) {
        super("" + index + ".", 0, 0, 0, 0);
        this.channel = channel;
        this.rotation = 45;
        this.tick = tick;
        this.value = value;
    }
    
    @Override
    public DisplayStyle getBorderDisplay(DisplayStyle display) {
        if (!modifiable)
            if (selected)
                return new ColoredDisplayStyle(140, 140, 40);
            else
                return new ColoredDisplayStyle(200, 200, 100);
        if (selected)
            return new ColoredDisplayStyle(40, 40, 140);
        if (isMouseOver())
            return new ColoredDisplayStyle(20, 20, 20);
        return super.getBorderDisplay(display);
    }
    
    @Override
    protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
        
    }
    
    @Override
    public boolean mousePressed(int x, int y, int button) {
        return true;
    }
    
    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add("" + value);
        return tooltip;
    }
    
    public void removeKey() {
        channel.removeKey(this);
        getParent().removeControl(this);
    }
    
    @Override
    public int compareTo(KeyControl o) {
        return Integer.compare(this.tick, o.tick);
    }
}
