package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.List;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.init.SoundEvents;

public class GuiComboBox extends GuiLabel {
    
    public GuiComboBoxExtension extension;
    public List<String> lines;
    
    public int index;
    
    public GuiComboBox(String name, int x, int y, int width, List<String> lines) {
        super(name, x, y, width, 14, ColorUtils.WHITE);
        this.lines = lines;
        
        if (lines.size() > 0) {
            this.caption = getDisplay(0);
            this.index = 0;
        } else {
            this.caption = "";
            this.index = -1;
        }
    }
    
    public String getDisplay(int index) {
        return lines.get(index);
    }
    
    public boolean select(int index) {
        if (index >= 0 && index < lines.size()) {
            caption = getDisplay(index);
            this.index = index;
            raiseEvent(new GuiControlChangedEvent(this));
            return true;
        }
        return false;
    }
    
    public boolean select(String line) {
        
        index = lines.indexOf(line);
        if (index != -1) {
            caption = lines.get(index);
            raiseEvent(new GuiControlChangedEvent(this));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasBorder() {
        return true;
    }
    
    @Override
    public boolean hasBackground() {
        return true;
    }
    
    @Override
    public boolean mousePressed(int posX, int posY, int button) {
        if (extension == null)
            openBox();
        else
            closeBox();
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    public void openBox() {
        this.extension = createBox();
        getGui().controls.add(extension);
        
        extension.parent = getGui();
        extension.moveControlToTop();
        extension.onOpened();
        getGui().refreshControls();
        extension.rotation = rotation;
        extension.posX = getPixelOffsetX() - getGui().getPixelOffsetX() - getContentOffset();
        extension.posY = getPixelOffsetY() - getGui().getPixelOffsetY() - getContentOffset() + height;
        
        if (extension.posY + extension.height > getParent().height && this.posY >= extension.height)
            extension.posY -= this.height + extension.height;
    }
    
    protected GuiComboBoxExtension createBox() {
        return new GuiComboBoxExtension(name + "extension", this, posX, posY + height, width - getContentOffset() * 2, 100, lines);
    }
    
    public void closeBox() {
        if (extension != null) {
            getGui().controls.remove(extension);
            extension = null;
        }
    }
}
