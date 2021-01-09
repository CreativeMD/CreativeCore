package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;

import net.minecraft.init.SoundEvents;

public class GuiComboBoxCategory<T> extends GuiLabel {
    
    public GuiComboBoxExtensionCategory<T> extension;
    public PairList<String, PairList<String, T>> elements;
    public int index;
    
    public GuiComboBoxCategory(String name, int x, int y, int width, PairList<String, PairList<String, T>> elements) {
        super(name, x, y, width, 14, ColorUtils.WHITE);
        this.elements = elements;
        if (!select(0)) {
            this.caption = "";
            this.index = -1;
        }
    }
    
    public Pair<String, T> getSelected() {
        if (index == -1)
            return null;
        int currentIndex = 0;
        for (Pair<String, PairList<String, T>> pair : elements) {
            if (index >= currentIndex + pair.value.size())
                currentIndex += pair.value.size();
            else
                return pair.value.get(index - currentIndex);
        }
        return null;
    }
    
    public boolean select(int index) {
        int currentIndex = 0;
        if (index == -1)
            return false;
        for (Pair<String, PairList<String, T>> pair : elements) {
            if (index >= currentIndex + pair.value.size())
                currentIndex += pair.value.size();
            else {
                caption = translate(pair.value.get(index - currentIndex).key);
                this.index = index;
                raiseEvent(new GuiControlChangedEvent(this));
                return true;
            }
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
    
    protected GuiComboBoxExtensionCategory<T> createBox() {
        return new GuiComboBoxExtensionCategory<T>(name + "extension", this, posX, posY + height, width - getContentOffset() * 2, 100);
    }
    
    public void closeBox() {
        if (extension != null) {
            getGui().controls.remove(extension);
            extension = null;
        }
    }
}
