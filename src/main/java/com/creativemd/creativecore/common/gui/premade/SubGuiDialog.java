package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.nbt.NBTTagCompound;

public class SubGuiDialog extends SubGui {
    
    public String[] buttons;
    public String[] lines;
    
    public SubGuiDialog(String[] lines, String[] buttons) {
        super("dialog", Math.max(GuiRenderHelper.instance.getStringWidth(lines) + 20, buttons.length * 30 + 20), lines.length * 20 + 40);
        this.lines = lines;
        this.buttons = buttons;
    }
    
    @Override
    public void createControls() {
        int height = 5;
        for (int i = 0; i < lines.length; i++) {
            controls.add(new GuiLabel(lines[i], 0, height, width - getContentOffset() * 2, GuiRenderHelper.instance.getFontHeight(), ColorUtils.WHITE));
            height += 20;
        }
        
        int buttonWidth = 0;
        GuiButton[] createdButtons = new GuiButton[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            GuiButton button = new GuiButton(buttons[i], 0, height + 5) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setString("clicked", this.caption);
                    closeLayer(nbt);
                }
            };
            buttonWidth += button.width + 2;
            createdButtons[i] = button;
            controls.add(button);
        }
        
        int x = width / 2 - buttonWidth / 2 + 1;
        for (int i = 0; i < createdButtons.length; i++) {
            createdButtons[i].posX = x;
            x += createdButtons[i].width + 2;
        }
    }
    
    public void saveData(NBTTagCompound nbt) {
        nbt.setBoolean("dialog", true);
        nbt.setString("text", String.join("\n", lines));
        nbt.setInteger("count", buttons.length);
        for (int i = 0; i < buttons.length; i++) {
            nbt.setString("b" + i, buttons[i]);
        }
        
    }
    
    @Override
    public void closeLayer(NBTTagCompound nbt, boolean isPacket) {
        saveData(nbt);
        super.closeLayer(nbt, isPacket);
    }
    
}
