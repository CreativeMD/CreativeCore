package com.creativemd.creativecore.common.gui.controls.gui.custom;

import java.util.ArrayList;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBox;

import net.minecraft.item.ItemStack;

public class GuiItemListBox extends GuiListBox {
	
	public ArrayList<ItemStack> stacks;
	
	public GuiItemListBox(String name, int x, int y, int width, int height, ArrayList<ItemStack> stacks, ArrayList<String> lines) {
		super(name, x, y, width, height, lines);
		this.stacks = stacks;
		reloadControls();
	}
	
	@Override
	public void clear() {
		stacks.clear();
		super.clear();
		reloadControls();
	}
	
	public void add(String input, ItemStack stack) {
		stacks.add(stack);
		lines.add(input);
		reloadControls();
	}
	
	public ItemStack getSelectedStack() {
		if (selected >= 0 && selected < stacks.size())
			return stacks.get(selected);
		return null;
	}
	
	@Override
	public void reloadControls() {
		controls.clear();
		if (stacks != null && stacks.size() == lines.size()) {
			for (int i = 0; i < lines.size(); i++) {
				int color = 14737632;
				if (i == selected)
					color = 16777000;
				GuiAvatarLabel label = new GuiAvatarLabel(lines.get(i), 3, 1 + i * 20, color, new AvatarItemStack(stacks.get(i))) {
					
					@Override
					public void onClicked(int x, int y, int button) {
						onLineClicked(this);
					}
				};
				label.width = width - 20;
				label.height = 20;
				controls.add(label);
				
			}
		}
		refreshControls();
	}
}
