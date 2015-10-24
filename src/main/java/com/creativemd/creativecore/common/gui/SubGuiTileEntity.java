package com.creativemd.creativecore.common.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiControl;

public abstract class SubGuiTileEntity extends SubGui{
	
	public TileEntity tileEntity;
	
	public SubGuiTileEntity(int width, int height, TileEntity tileEntity)
	{
		super(width, height);
		this.tileEntity = tileEntity;
	}
	
	public SubGuiTileEntity(TileEntity tileEntity)
	{
		super();
		this.tileEntity = tileEntity;
	}
	
	@Override
	public void readFromOpeningNBT(NBTTagCompound nbt)
	{
		tileEntity.readFromNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		tileEntity.readFromNBT(nbt);
	}

}
