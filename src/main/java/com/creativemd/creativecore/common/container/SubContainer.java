package com.creativemd.creativecore.common.container;

import java.util.ArrayList;

import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.container.slot.SlotControl;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.GuiUpdatePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class SubContainer{
	
	/**TickCount for sending UpdatePacket*/
	//@SideOnly(Side.SERVER)
	public int tick;
	
	public EntityPlayer player;
	
	public SubContainer(EntityPlayer player)
	{
		this.player = player;
		this.tick = 0;
	}
	
	public ContainerSub container;
	
	//public ArrayList<IInventory> inventories = new ArrayList<IInventory>();
	
	public ArrayList<ContainerControl> controls = new ArrayList<ContainerControl>();
	
	/**Primary use to add slots*/
	public abstract void createControls();
	
	public abstract void onGuiPacket(int controlID, NBTTagCompound nbt, EntityPlayer player);
	
	/**0: no update, standard: 10->0.5 secs*/
	public int getUpdateTick(){
		return 10;
	}
	
	/**Called once a slot changes*/
	public void onSlotChange() {}
	
	public void onGuiClosed()
	{
		
	}
	
	public void onGuiOpened()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			writeOpeningNBT(nbt);
			PacketHandler.sendPacketToPlayer(new GuiUpdatePacket(nbt, true), (EntityPlayerMP) player);
		}
	}
	
	/**Can be used to update the gui per Tick**/	
	public void onUpdate()
	{
		if(getUpdateTick() > 0 && FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			tick++;
			if(tick > getUpdateTick())
			{
				tick = 0;
				sendUpdate();
			}
		}
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).detectChange();
		}
	}
	
	public void sendUpdate()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		PacketHandler.sendPacketToPlayer(new GuiUpdatePacket(nbt, false), (EntityPlayerMP) player);
	}
	
	/**Called once a player connects*/
	public void writeOpeningNBT(NBTTagCompound nbt){}
	
	/**Called for every Tick-Update*/
	public void writeToNBT(NBTTagCompound nbt){}
		
	/*=============================Helper Methods=============================*/
	
	public ArrayList<Slot> getSlots()
	{
		ArrayList<Slot> slots = new ArrayList<Slot>();
		for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i) instanceof SlotControl)
				slots.add(((SlotControl)controls.get(i)).slot);
		}
		return slots;
	}
	
	public void addSlotToContainer(Slot slot)
	{
		slot.xDisplayPosition += 8;
		slot.yDisplayPosition += 8;
		//if(!inventories.contains(slot.inventory))
			//inventories.add(slot.inventory);
		controls.add(new SlotControl(slot));
	}
	
	public void addPlayerSlotsToContainer(EntityPlayer player)
	{
		addPlayerSlotsToContainer(player, 8, 84);
	}
	
	public void addPlayerSlotsToContainer(EntityPlayer player, int x, int y)
	{
		int l;
        for (l = 0; l < 3; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
            	addSlotToContainer(new Slot(player.inventory, i1 + l * 9 + 9, i1 * 18 + x, l * 18+y));
            }
        }

        for (l = 0; l < 9; ++l)
        {
        	addSlotToContainer(new Slot(player.inventory, l, l * 18+x, 58+y));
        }
	}
}
