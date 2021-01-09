package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public abstract class GuiButton extends GuiClickableLabel {
    
    public GuiButton(String caption, int x, int y) {
        this(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption));
    }
    
    public GuiButton(String name, String title, int x, int y) {
        this(name, title, x, y, GuiRenderHelper.instance.getStringWidth(title));
    }
    
    public GuiButton(String caption, int x, int y, int width) {
        this(caption, caption, x, y, width, 14);
    }
    
    public GuiButton(String name, String caption, int x, int y, int width) {
        this(name, caption, x, y, width, 14);
    }
    
    public GuiButton(String name, String caption, int x, int y, int width, int height) {
        super(name, caption, x, y, width, height, ColorUtils.WHITE);
    }
    
    public GuiButton(String caption, int x, int y, int width, int height) {
        this(caption, caption, x, y, width, height);
    }
    
    @Override
    public boolean hasBorder() {
        return true;
    }
    
    @Override
    public boolean hasBackground() {
        return true;
    }
    
}
