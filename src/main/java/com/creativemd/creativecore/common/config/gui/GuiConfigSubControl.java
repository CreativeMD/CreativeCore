package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.container.GuiParent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigSubControl extends GuiParent {
    
    public GuiConfigSubControl(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.borderWidth = this.marginWidth = 0;
    }
    
    @Override
    public boolean hasBorder() {
        return false;
    }
    
    @Override
    public boolean hasBackground() {
        return false;
    }
    
    @Override
    public boolean hasMouseOverEffect() {
        return false;
    }
    
}
