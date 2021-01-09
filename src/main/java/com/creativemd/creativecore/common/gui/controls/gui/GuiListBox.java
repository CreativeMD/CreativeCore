package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public class GuiListBox extends GuiScrollBox {
    
    protected List<String> lines = new ArrayList<String>();
    
    public int selected = -1;
    
    public GuiListBox(String name, int x, int y, int width, int height, List<String> lines) {
        super(name, x, y, width, height);
        this.lines = lines;
        reloadControls();
    }
    
    public String getSelected() {
        if (selected != -1)
            return get(selected);
        return "";
    }
    
    public void clear() {
        lines.clear();
        selected = -1;
        maxScroll = 0;
        scrolled.setStart(0);
        reloadControls();
    }
    
    public int size() {
        return lines.size();
    }
    
    public String get(int id) {
        return lines.get(id);
    }
    
    public void add(String input) {
        lines.add(input);
        reloadControls();
    }
    
    public void remove(int id) {
        lines.remove(id);
        reloadControls();
    }
    
    public void onLineClicked(GuiControl control) {
        int index = controls.indexOf(control);
        
        if (index != -1) {
            if (selected != -1 && selected < controls.size())
                ((GuiLabel) controls.get(selected)).color = 14737632;
            selected = index;
            ((GuiLabel) controls.get(selected)).color = 16777000;
            
            onSelectionChange();
        }
    }
    
    public void reloadControls() {
        controls.clear();
        for (int i = 0; i < lines.size(); i++) {
            int color = 14737632;
            if (i == selected)
                color = 16777000;
            GuiClickableLabel label = new GuiClickableLabel(lines.get(i), 0, i * 15, width - scrollbarWidth - 2 - getContentOffset() * 2, 8, color) {
                
                @Override
                public int getColor() {
                    if (isMouseOver() && color != 16777000)
                        return ColorUtils.RGBAToInt(new Color(255, 255, 100));
                    return color;
                }
                
                @Override
                public void onClicked(int x, int y, int button) {
                    onLineClicked(this);
                }
            };
            controls.add(label);
        }
        refreshControls();
    }
    
    public void onSelectionChange() {
        raiseEvent(new GuiControlChangedEvent(this));
    }
}
