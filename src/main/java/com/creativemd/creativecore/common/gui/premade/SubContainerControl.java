package com.creativemd.creativecore.common.gui.premade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.creativemd.creativecore.common.container.SubContainer;

public class SubContainerControl extends SubContainer{

	public SubContainerControl(EntityPlayer player) {
		super(player);
	}

	@Override
	public void createControls() {
		
	}

	@Override
	public void onGuiPacket(int controlID, NBTTagCompound nbt,
			EntityPlayer player) {
		
	}

}
