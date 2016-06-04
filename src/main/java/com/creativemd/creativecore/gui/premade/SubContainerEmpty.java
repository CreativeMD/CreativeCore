package com.creativemd.creativecore.gui.premade;

import com.creativemd.creativecore.gui.container.SubContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerEmpty extends SubContainer{

	public SubContainerEmpty(EntityPlayer player) {
		super("empty", player);
	}

	@Override
	public void createControls() {
		
	}
	
	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		
	}

}
