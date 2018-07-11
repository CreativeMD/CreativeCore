package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.utils.type.HashMapList;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class GuiStackSelector extends GuiComboBox {
	
	protected HashMapList<String, ItemStack> stacks;
	
	public EntityPlayer player;
	
	public GuiStackSelector(String name, int x, int y, int width, EntityPlayer player)
	{
		super(name, x, y, width, new ArrayList<String>());
		this.player = player;
		updateCollectedStacks();
		selectFirst();
	}
	
	public boolean selectFirst()
	{
		if(stacks != null)
		{
			ItemStack first = stacks.getFirst();
			if(first != null)
			{
				setSelected(first);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return player;
	}
	
	protected abstract HashMapList<String, ItemStack> collectItems();
	
	public void updateCollectedStacks()
	{
		stacks = collectItems();
	}
	
	protected ItemStack selected = ItemStack.EMPTY;
	
	public boolean setSelected(ItemStack stack)
	{
		if(stacks.contains(stack))
		{
			String display;
			try{
				display = stack.getDisplayName();
			}catch(Exception e){
				display = Item.REGISTRY.getNameForObject(stack.getItem()).toString();
			}
			caption = display;
			this.selected = stack;
			raiseEvent(new GuiControlChangedEvent(this));
			return true;
		}
		return false;
	}
	
	public HashMapList<String, ItemStack> getStacks()
	{
		return stacks;
	}
	
	public ItemStack getSelected()
	{
		return selected;
	}
	
	@Override
	protected int getAdditionalSize()
	{
		return 16+6;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		super.renderContent(helper, style, width, height);
		
		ItemStack stack = getSelected();
		if(!stack.isEmpty())
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(width/2-(helper.getStringWidth(caption)+getAdditionalSize())/2, height/2-16/2, 0);
			helper.drawItemStack(stack, 0, 0, 16, 16);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	protected GuiComboBoxExtension createBox()
	{
		return new GuiStackSelectorExtension(name + "extension", getPlayer(), posX, posY+height, width-getContentOffset()*2, 80, this);
	}
	
	public boolean select(String line)
	{
		return false;
	}
	
}
