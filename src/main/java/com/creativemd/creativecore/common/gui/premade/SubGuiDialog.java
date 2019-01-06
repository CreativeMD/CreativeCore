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
		
		for (int i = 0; i < buttons.length; i++) {
			controls.add(new GuiButton(buttons[i], 30 * i + (width / 2 - buttons.length * 30 / 2), height + 5, 24) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("clicked", this.caption);
					closeLayer(nbt);
				}
			});
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
