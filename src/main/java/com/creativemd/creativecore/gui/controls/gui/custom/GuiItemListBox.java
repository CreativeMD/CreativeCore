package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiItemListBox extends GuiListBox{
	
	public ArrayList<ItemStack> stacks;
	
	public GuiItemListBox(String name, EntityPlayer player, int x, int y, int width, int height, ArrayList<ItemStack> stacks,  ArrayList<String> lines) {
		super(name, player, x, y, width, height, lines);
		this.stacks = stacks;
		refreshControls();
	}
	
	@Override
	public void clear()
	{
		stacks.clear();
		super.clear();
		refreshControls();
	}
	
	public void add(String input, ItemStack stack)
	{
		stacks.add(stack);
		lines.add(input);
		refreshControls();
	}
	
	public ItemStack getSelectedStack()
	{
		if(selected >= 0 && selected < stacks.size())
			return stacks.get(selected);
		return null;
	}
	
	@Override
	public void refreshControls()
	{
		gui.controls.clear();
		if(stacks != null && stacks.size() == lines.size())
		{
			for (int i = 0; i < lines.size(); i++) {
				int color = 14737632;
				if(i == selected)
					color = 16777000;
				GuiAvatarLabel label = new GuiAvatarLabel(lines.get(i), 3, 1+i*20, color, new AvatarItemStack(stacks.get(i)));
				label.width = width-20;
				label.height = 20;
				addControl(label);
			}
		}
	}
}
