package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.GuiComboBoxExtension;
import com.creativemd.creativecore.common.gui.controls.GuiLabelClickable;

public class GuiInvSelectorExtension extends GuiComboBoxExtension{
	
	public ArrayList<ItemStack> stacks;

	public GuiInvSelectorExtension(String name, EntityPlayer player, GuiComboBox comboBox, int x, int y, int width, int height, ArrayList<String> lines, ArrayList<ItemStack> stacks) {
		super(name, player, comboBox, x, y, width, height, lines);
		this.stacks = stacks;
		refreshControls();
	}
	
	
	@Override
	public void refreshControls()
	{
		if(stacks != null)
		{
			gui.controls.clear();
			for (int i = 0; i < lines.size(); i++) {
				int color = 14737632;
				if(i == selected)
					color = 16777000;
				GuiLabelClickable label = new GuiAvatarLabel(lines.get(i), 3, 1+i*22, color, new AvatarItemStack(stacks.get(i)));
				label.width = width-20;
				label.height = 22;
				addControl(label);
			}
		}
	}
}
