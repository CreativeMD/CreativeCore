package com.creativemd.creativecore.gui.premade;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;

import net.minecraft.nbt.NBTTagCompound;

public class SubGuiDialog extends SubGui {
	
	public String[] buttons;
	public String text;
	
	public SubGuiDialog(String text, String[] buttons) {
		super("dialog", Math.max(GuiRenderHelper.instance.getStringWidth(text) + 20, buttons.length * 30 + 20), 60);
		this.text = text;
		this.buttons = buttons;
	}
	
	@Override
	public void createControls() {
		controls.add(new GuiLabel(text, 0, 5));
		for (int i = 0; i < buttons.length; i++) {
			controls.add(new GuiButton(buttons[i], 30 * i + (width / 2 - buttons.length * 30 / 2), 30, 24) {
				
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
		nbt.setString("text", text);
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
