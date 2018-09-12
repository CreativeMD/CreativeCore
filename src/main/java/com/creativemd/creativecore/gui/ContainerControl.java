package com.creativemd.creativecore.gui;

import com.creativemd.creativecore.gui.container.ContainerParent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ContainerControl extends CoreControl {

	public ContainerControl(String name) {
		super(name);
	}

	// ================Helper================

	public ContainerParent getParent() {
		return (ContainerParent) parent;
	}

	// ================Packets================

	public void sendUpdate() {
		getParent().updateEqualContainers();

		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBTUpdate(nbt);
		sendPacket(nbt);
	}

	public void sendPacket(NBTTagCompound nbt) {
		if (parent != null)
			getParent().sendNBTUpdate(this, nbt);
	}

	// ================Update Packet================

	public abstract void writeToNBTUpdate(NBTTagCompound nbt);

	public void receivePacket(NBTTagCompound nbt) {
		onPacketReceive(nbt);
	}

	public abstract void onPacketReceive(NBTTagCompound nbt);

	// ================Client/Gui================

	@SideOnly(Side.CLIENT)
	protected GuiControl guiControl;

	@SideOnly(Side.CLIENT)
	protected abstract GuiControl createGuiControl();

	@SideOnly(Side.CLIENT)
	public GuiControl getGuiControl() {
		if (guiControl == null)
			guiControl = createGuiControl();
		return guiControl;
	}

}
