package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.container.SubContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerEmpty extends SubContainer {
    
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
