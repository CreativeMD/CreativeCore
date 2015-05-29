package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiControl;

public abstract class SubGuiTileEntity extends SubGui{
	
	public TileEntity tileEntity;
	
	public SubGuiTileEntity(TileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
	}

}
