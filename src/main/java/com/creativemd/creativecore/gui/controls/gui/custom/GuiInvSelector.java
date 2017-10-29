package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class GuiInvSelector extends GuiComboBox{
	
	public static class StackSelector {
		
		public boolean allow(ItemStack stack)
		{
			return true;
		}
		
	}
	
	public ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
	
	public StackSelector selector;
	
	public String search;
	
	public GuiInvSelector(String name, int x, int y, int width, EntityPlayer player, boolean onlyBlocks) {	
		this(name, x, y, width, player, onlyBlocks, "");
	}
	
	public GuiInvSelector(String name, int x, int y, int width, EntityPlayer player, boolean onlyBlocks, String search) {		
		super(name, x, y, width, new ArrayList<String>());
		this.selector = new StackSelector(){
			
			@Override
			public boolean allow(ItemStack stack) {
				return GuiItemStackSelector.shouldShowItem(onlyBlocks, GuiInvSelector.this.search, stack);
			}
			
		};
		this.search = search;
		updateItems(player);
		
	}
	
	public GuiInvSelector(String name, int x, int y, int width, EntityPlayer player, StackSelector selector) {
		
		super(name, x, y, width, new ArrayList<String>());
		this.selector = selector;
		updateItems(player);
	}
	
	public void updateItems(EntityPlayer player)
	{
		NonNullList<ItemStack> newStacks = NonNullList.create();
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			if(!player.inventory.mainInventory.get(i).isEmpty())
				newStacks.add(player.inventory.mainInventory.get(i).copy());
		}
		
		Iterator iterator = Item.REGISTRY.iterator();
		while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, (CreativeTabs)null, newStacks);
            }
        }
		
		iterator = Block.REGISTRY.iterator();
		while (iterator.hasNext())
        {
			Block block = (Block)iterator.next();

            if (block != null && block.getCreativeTabToDisplayOn() != null)
            {
            	block.getSubBlocks(Item.getItemFromBlock(block), (CreativeTabs)null, newStacks);
            }
        }
		
		stacks.clear();
		lines.clear();
		
		for (int i = 0; i < newStacks.size(); i++) {
			if(selector.allow(newStacks.get(i)))
			{
				stacks.add(newStacks.get(i));
				lines.add(GuiItemStackSelector.getItemName(newStacks.get(i)));
			}
		}
		
		if(lines.size() > 0)
		{
			index = 0;
			caption = lines.get(0);
		}
		else{
			caption = "";
			index = -1;
		}
	}
	
	public void addAndSelectStack(ItemStack stack)
	{
		try{
			lines.add(stack.getDisplayName());
		}catch(Exception e){
			lines.add(Item.REGISTRY.getNameForObject(stack.getItem()).toString());
		}
		stacks.add(stack.copy());
		caption = lines.get(lines.size()-1);
		index = lines.size()-1;
		
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	/*@Override
	public void drawControl(FontRenderer renderer) {
		Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d color = new Vector4d(60, 60, 60, 255);
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, color, color);
		
		if(index != -1 && stacks.size() > index && stacks.get(index) != null)
		{
			Avatar avatar = new AvatarItemStack(stacks.get(index));
			GL11.glTranslated(1, 1, 0);
			avatar.handleRendering(mc, renderer, 18, 18);
		}
		renderer.drawString(caption, 4+20, height/2-renderer.FONT_HEIGHT/2, 14737632);
	}*/
	
	@Override
	protected int getAdditionalSize()
	{
		return 16+6;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		super.renderContent(helper, style, width, height);
		
		ItemStack stack = getStack();
		if(stack != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(width/2-(helper.getStringWidth(caption)+getAdditionalSize())/2, height/2-16/2, 0);
			helper.drawItemStack(stack, 0, 0, 16, 16);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public void openBox()
	{
		extension = new GuiItemStackSelector(name + "extension", getPlayer(), posX, posY+height, width-getContentOffset()*2, 80, this, selector);
		//extension = new GuiInvSelectorExtension(name + "extension", parent.container.player, this, posX, posY+height, width, 150, lines, stacks);
		getParent().controls.add(extension);
		
		extension.parent = parent;
		extension.moveControlToTop();
		extension.onOpened();
		parent.refreshControls();
		extension.rotation = rotation;
	}
	
	public ItemStack getStack()
	{
		if(index != -1 && index < stacks.size())
			return stacks.get(index);
		return null;
	}

}
