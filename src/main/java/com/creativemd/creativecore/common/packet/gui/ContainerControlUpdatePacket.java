package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.mc.ContainerSub;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerControlUpdatePacket extends CreativeCorePacket {
	
	public NBTTagCompound value;
	public String name;
	public int layer;
	
	public ContainerControlUpdatePacket() {
		super();
	}
	
	public ContainerControlUpdatePacket(int layer, String name, NBTTagCompound value) {
		super();
		this.value = value;
		this.name = name;
		this.layer = layer;
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		writeNBT(bytes, value);
		bytes.writeBoolean(name != null);
		if (name != null)
			writeString(bytes, name);
		bytes.writeInt(layer);
	}
	
	@Override
	public void readBytes(ByteBuf bytes) {
		value = ByteBufUtils.readTag(bytes);
		if (bytes.readBoolean())
			name = readString(bytes);
		layer = bytes.readInt();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub) {
			CoreControl control = ((ContainerSub) player.openContainer).layers.get(layer);
			if (name != null)
				control = ((SubContainer) control).get(name);
			if (control instanceof ContainerControl)
				((ContainerControl) control).receivePacket(value);
		}
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub) {
			CoreControl control = ((ContainerSub) player.openContainer).layers.get(layer);
			if (name != null)
				control = ((SubContainer) control).get(name);
			if (control instanceof ContainerControl)
				((ContainerControl) control).receivePacket(value);
		}
	}
}
