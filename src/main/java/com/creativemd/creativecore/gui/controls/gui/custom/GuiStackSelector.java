package com.creativemd.creativecore.gui.controls.gui.custom;

import net.minecraft.entity.player.EntityPlayer;

public class GuiStackSelector extends GuiInvSelector{

	public GuiStackSelector(String name, int x, int y, int width, EntityPlayer player, boolean onlyBlocks) {
		super(name, x, y, width, player, onlyBlocks);
	}
	
	@Override
	public void openBox()
	{
		extension = new GuiInvSelectorExtension(name + "extension", this, posX, posY+height, width-getContentOffset()*2, 80, lines, stacks);
		//extension = new GuiInvSelectorExtension(name + "extension", parent.container.player, this, posX, posY+height, width, 150, lines, stacks);
		getParent().controls.add(extension);
		
		extension.parent = parent;
		extension.moveControlToTop();
		extension.onOpened();
		parent.refreshControls();
		extension.rotation = rotation;
	}

}
