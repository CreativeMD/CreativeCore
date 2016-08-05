package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBoxExtension;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiInvSelectorExtension extends GuiComboBoxExtension{
	
	public ArrayList<ItemStack> stacks;

	public GuiInvSelectorExtension(String name, GuiComboBox comboBox, int x, int y, int width, int height, ArrayList<String> lines, ArrayList<ItemStack> stacks) {
		super(name, comboBox, x, y, width, height, lines);
		this.stacks = stacks;
		reloadControls();
	}
	
	
	@Override
	public void reloadControls()
	{
		if(stacks != null)
		{
			controls.clear();
			for (int i = 0; i < lines.size(); i++) {
				int color = 14737632;
				if(i == selected)
					color = 16777000;
				GuiAvatarLabel label = new GuiAvatarLabel(lines.get(i), 3, 1+i*22, color, new AvatarItemStack(stacks.get(i))){
					
					@Override
					public void onClicked(int x, int y, int button) {
						onLineClicked(this);
					}
				};
				label.width = width-20;
				label.height = 22;
				controls.add(label);
			}
		}
		refreshControls();
	}
}
