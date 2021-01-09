package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.util.math.Vec3d;

public class GuiComboBoxExtensionCategory<T> extends GuiScrollBox {
    
    public GuiComboBoxCategory<T> comboBox;
    public Pair<String, T> selected;
    
    public GuiComboBoxExtensionCategory(String name, GuiComboBoxCategory<T> comboBox, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        this.comboBox = comboBox;
        this.selected = comboBox.getSelected();
        reloadControls();
    }
    
    @Override
    public Vec3d getCenterOffset() {
        return new Vec3d(width / 2, -comboBox.height / 2, 0);
    }
    
    public void onLoseFocus() {
        if (!comboBox.isMouseOver() && !isMouseOver())
            comboBox.closeBox();
    }
    
    public void reloadControls() {
        controls.clear();
        
        int index = 0;
        int height = 0;
        for (Pair<String, PairList<String, T>> category : comboBox.elements) {
            if (category.key != null && !category.key.isEmpty()) {
                //, width - scrollbarWidth - 2 - getContentOffset() * 2, 8, ColorUtils.WHITE
                controls.add(new GuiLabel(category.key, ChatFormatting.UNDERLINE + "" + ChatFormatting.ITALIC + translate(category.key), 0, height));
                height += 15;
            }
            
            for (Pair<String, T> pair : category.value) {
                int color = 14737632;
                if (pair == selected)
                    color = 16777000;
                int itemIndex = index;
                GuiClickableLabel label = new GuiClickableLabel(pair.key, translate(pair.key), 0, height, width - scrollbarWidth - 2 - getContentOffset() * 2, 8, color) {
                    
                    @Override
                    public int getColor() {
                        if (isMouseOver() && color != 16777000)
                            return ColorUtils.RGBAToInt(new Color(255, 255, 100));
                        return color;
                    }
                    
                    @Override
                    public void onClicked(int x, int y, int button) {
                        onSelectionChange(itemIndex);
                    }
                };
                controls.add(label);
                index++;
                height += 15;
            }
        }
        refreshControls();
    }
    
    public void onSelectionChange(int index) {
        if (index != -1)
            comboBox.select(index);
        comboBox.closeBox();
    }
    
    @Override
    public boolean canOverlap() {
        return true;
    }
    
}
