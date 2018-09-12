package com.creativemd.creativecore.gui.controls.container;

import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class SlotControlNoSync extends SlotControl {
	
	public SlotControlNoSync(Slot slot) {
		super(slot);
	}
	
	@Override
	public void sendPacket(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBTUpdate(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void sendUpdate() {
		
	}
	
	@Override
	public void onSlotClicked(int mouseButton, boolean shift, int scrolled) {
		
	}
	
}
