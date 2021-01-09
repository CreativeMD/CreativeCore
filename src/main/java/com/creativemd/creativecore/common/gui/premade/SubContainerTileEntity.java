package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class SubContainerTileEntity extends SubContainer {
    
    protected int currenTick;
    public TileEntity te;
    
    public SubContainerTileEntity(EntityPlayer player, TileEntity te) {
        super(player);
        this.te = te;
    }
    
    @Override
    public void writeOpeningNBT(NBTTagCompound nbt) {
        writeToNBTUpdate(nbt);
    }
    
    @Override
    public void writeToNBTUpdate(NBTTagCompound nbt) {
        nbt.setBoolean("teUpdate", true);
        te.writeToNBT(nbt);
    }
    
    public void receivePacket(NBTTagCompound nbt) {
        if (nbt.getBoolean("teUpdate")) {
            te.readFromNBT(nbt);
        } else
            super.receivePacket(nbt);
    }
    
    @Override
    public void onOpened() {
        super.onOpened();
        container.coord = te.getPos();
    }
    
    public boolean shouldTick() {
        return true;
    }
    
    public int getUpdateTickRate() {
        return 10;
    }
    
    public void onTick() {
        if (shouldTick()) {
            currenTick++;
            if (currenTick > getUpdateTickRate()) {
                sendUpdate();
                currenTick = 0;
            }
        }
    }
    
    @Override
    public void updateEqualContainers() {
        for (int i = 0; i < GuiHandler.openContainers.size(); i++) {
            if (GuiHandler.openContainers.get(i).coord.equals(container.coord))
                for (int j = 0; j < GuiHandler.openContainers.get(i).layers.size(); j++) {
                    GuiHandler.openContainers.get(i).layers.get(j).onOpened();
                }
        }
    }
    
}
