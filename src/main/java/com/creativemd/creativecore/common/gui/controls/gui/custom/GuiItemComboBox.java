package com.creativemd.creativecore.common.gui.controls.gui.custom;

import java.util.List;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.item.ItemStack;

public class GuiItemComboBox extends GuiComboBox {
    
    public List<ItemStack> stacks;
    
    public GuiItemComboBox(String name, int x, int y, int width, List<String> lines, List<ItemStack> stacks) {
        super(name, x, y, width, lines);
        this.stacks = stacks;
    }
    
    @Override
    protected GuiComboBoxExtension createBox() {
        return new GuiItemComboBoxExtension(name + "extension", this, posX, posY + height, width - getContentOffset() * 2, 100, lines, stacks);
    }
    
    public static class GuiItemComboBoxExtension extends GuiComboBoxExtension {
        
        public List<ItemStack> stacks;
        
        public GuiItemComboBoxExtension(String name, GuiItemComboBox comboBox, int x, int y, int width, int height, List<String> lines, List<ItemStack> stacks) {
            super(name, comboBox, x, y, width, height, lines);
            this.stacks = stacks;
            reloadControls();
        }
        
        @Override
        public void reloadControls() {
            controls.clear();
            if (stacks != null && stacks.size() == lines.size()) {
                for (int i = 0; i < lines.size(); i++) {
                    int color = 14737632;
                    if (i == selected)
                        color = 16777000;
                    int index = i;
                    GuiAvatarLabel label = new GuiAvatarLabel(lines.get(i), lines.get(i), 3, 1 + i * 20, color, new AvatarItemStack(stacks.get(i)), false) {
                        
                        @Override
                        public void onClicked(int x, int y, int button) {
                            selected = index;
                            onSelectionChange();
                        }
                        
                        @Override
                        public int getColor() {
                            if (selected == index || !isMouseOver())
                                return color;
                            return ColorUtils.RGBAToInt(255, 255, 100, 255);
                        }
                        
                    };
                    label.width = width - 10;
                    label.height = 20;
                    controls.add(label);
                    
                }
            }
            refreshControls();
        }
        
    }
}
