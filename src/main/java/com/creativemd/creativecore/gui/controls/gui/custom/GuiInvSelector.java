package com.creativemd.creativecore.gui.controls.gui.custom;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.controls.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.GuiComboBoxExtension;
import com.creativemd.creativecore.common.gui.controls.GuiControl;

public class GuiInvSelector extends GuiComboBox{
	
	public ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
	
	public boolean onlyBlocks;
	public String search;
	
	public GuiInvSelector(String name, int x, int y, int width, EntityPlayer player, boolean onlyBlocks, String search) {
		super(name, x, y, width, new ArrayList<String>());
		this.search = search;
		this.onlyBlocks = onlyBlocks;
		updateItems(player);
		
	}
	
	public GuiInvSelector(String name, int x, int y, int width, EntityPlayer player, boolean onlyBlocks) {
		
		this(name, x, y, width, player, onlyBlocks, "");
	}
	
	public void updateItems(EntityPlayer player)
	{
		boolean shouldSearch = search.equals("");
		ArrayList<ItemStack> newStacks = new ArrayList<ItemStack>();
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			if(player.inventory.mainInventory[i] != null)
			{
				newStacks.add(player.inventory.mainInventory[i].copy());
			}
		}
		
		Iterator iterator = Item.itemRegistry.iterator();
		while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item != null && item.getCreativeTab() != null)
            {
                item.getSubItems(item, (CreativeTabs)null, newStacks);
            }
        }
		
		stacks.clear();
		lines.clear();
		
		for (int i = 0; i < newStacks.size(); i++) {
			if(GuiItemStackSelector.shouldShowItem(onlyBlocks, search, newStacks.get(i)))
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
			lines.add(Item.itemRegistry.getNameForObject(stack.getItem()));
		}
		stacks.add(stack.copy());
		caption = lines.get(lines.size()-1);
		index = lines.size()-1;
	}
	
	@Override
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
	}
	
	@Override
	public void openBox()
	{
		extension = new GuiItemStackSelector(name + "extension", parent.container.player, posX, posY+height, width, 200, this, onlyBlocks, search);
		//extension = new GuiInvSelectorExtension(name + "extension", parent.container.player, this, posX, posY+height, width, 150, lines, stacks);
		parent.controls.add(extension);
		
		extension.parent = parent;
		extension.moveControlToTop();
		extension.init();
		parent.refreshControls();
		extension.rotation = rotation;
	}
	
	public ItemStack getStack()
	{
		if(index != -1)
			return stacks.get(index);
		return null;
	}

}
