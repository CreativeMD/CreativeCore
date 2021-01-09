package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiNBTPacket extends CreativeCorePacket {
    
    public NBTTagCompound value;
    
    public GuiNBTPacket(NBTTagCompound nbt) {
        this.value = nbt;
    }
    
    public GuiNBTPacket() {
        
    }
    
    @Override
    public void writeBytes(ByteBuf buf) {
        writeNBT(buf, value);
    }
    
    @Override
    public void readBytes(ByteBuf buf) {
        value = readNBT(buf);
    }
    
    @Override
    public void executeClient(EntityPlayer player) {
        if (player.openContainer instanceof ContainerSub)
            ((ContainerSub) player.openContainer).gui.getTopLayer().receiveContainerPacket(value);
    }
    
    @Override
    public void executeServer(EntityPlayer player) {
        
    }
    
}
