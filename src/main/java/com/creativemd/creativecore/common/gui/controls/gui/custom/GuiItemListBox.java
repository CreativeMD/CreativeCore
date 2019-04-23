package com.creativemd.creativecore.common.gui.controls.gui.custom;

import java.util.List;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBox;

import net.minecraft.item.ItemStack;

public class GuiItemListBox extends GuiListBox {
	
	public List<ItemStack> stacks;
	public boolean centered;
	
	public GuiItemListBox(String name, int x, int y, int width, int height, List<ItemStack> stacks, List<String> lines, boolean centered) {
		super(name, x, y, width, height, lines);
		this.stacks = stacks;
		this.centered = centered;
		reloadControls();
	}
	
	public GuiItemListBox(String name, int x, int y, int width, int height, List<ItemStack> stacks, List<String> lines) {
		this(name, x, y, width, height, stacks, lines, true);
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
				int index = i;
				GuiAvatarLabel label = new GuiAvatarLabel(lines.get(i), 3, 1 + i * 20, color, new AvatarItemStack(stacks.get(i))) {
					
					@Override
					public void onClicked(int x, int y, int button) {
						if (selected != -1 && selected < controls.size())
							((GuiLabel) controls.get(selected)).color = 14737632;
						selected = index;
						((GuiLabel) controls.get(selected)).color = 16777000;
						
						onSelectionChange();
						
					}
				};
				if (centered)
					label.width = width - 20;
				label.height = 20;
				controls.add(label);
				
			}
		}
		refreshControls();
	}
}
