package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.container.SubContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CustomGuiHandler {
	
	public abstract SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt);
	
	@SideOnly(Side.CLIENT)
	public abstract SubGui getGui(EntityPlayer player, NBTTagCompound nbt);
}
