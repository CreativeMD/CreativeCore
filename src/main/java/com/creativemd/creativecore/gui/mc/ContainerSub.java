package com.creativemd.creativecore.gui.mc;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.opener.GuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSub extends Container{
	
	public ArrayList<SubContainer> layers;
	
	@SideOnly(Side.CLIENT)
	public GuiContainerSub gui;
	
	public BlockPos coord = null;
	
	public ContainerSub(EntityPlayer player, SubContainer subContainer)
	{
		layers = new ArrayList<SubContainer>();
		
		subContainer.container = this;
		
		layers.add(subContainer);
		subContainer.onOpened();
	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	public SubContainer getTopLayer()
	{
		return layers.get(layers.size()-1);
	}
	
	public boolean isTopLayer(SubContainer container)
	{
		return getTopLayer() == container;
	}
	
	@Override
	public void detectAndSendChanges()
    {
		super.detectAndSendChanges();
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).onTick();
		}
    }
	
	@Override
	public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        for (int i = 0; i < layers.size(); i++) {
			layers.get(i).onClosed();
		}
        GuiHandler.openContainers.remove(this);
    }

}
