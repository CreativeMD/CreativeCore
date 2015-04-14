package com.creativemd.creativecore.common.container;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public abstract class SubContainer{
	
	public ContainerSub container;
	
	public ArrayList<IInventory> inventories = new ArrayList<IInventory>();
	
	public ArrayList<Slot> slots = new ArrayList<Slot>();
	
	public abstract void onGuiPacket(int control, String value, EntityPlayer player);
	
	public abstract ArrayList<Slot> getSlots(EntityPlayer player);
	
	public abstract boolean doesGuiNeedUpdate();
	
	/**Can be used to update the gui per Tick**/	
	public void onUpdate()
	{
		
	}
	
	public ArrayList<Slot> getPlayerSlots(EntityPlayer player)
	{
		return getPlayerSlots(player, 8, 84);
	}
	
	public ArrayList<Slot> getPlayerSlots(EntityPlayer player, int x, int y)
	{
		ArrayList<Slot> slots = new ArrayList<Slot>();
		int l;
        for (l = 0; l < 3; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
            	slots.add(new Slot(player.inventory, i1 + l * 9 + 9, i1 * 18 + x, l * 18+y));
            }
        }

        for (l = 0; l < 9; ++l)
        {
        	slots.add(new Slot(player.inventory, l, l * 18+x, 58+y));
        }
        return slots;
	}
}
