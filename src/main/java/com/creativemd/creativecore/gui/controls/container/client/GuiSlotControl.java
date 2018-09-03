package com.creativemd.creativecore.gui.controls.container.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.SidedColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.client.style.TextureDisplayStyle;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.container.SlotControl;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.slots.SlotInput;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiSlotControl extends GuiControl{
	
	public static Style slotStyle = new Style("slot", new TextureDisplayStyle(guiUtilsImage, 176, 0), DisplayStyle.emptyDisplay, new ColoredDisplayStyle(197, 197, 197),
			new ColoredDisplayStyle(139, 139, 139), new ColoredDisplayStyle(0, 0, 0, 100));
	
	public SlotControl slot;
	
	public GuiSlotControl(int x, int y, SlotControl slot) {
		super(slot.slot.inventory.getName() + slot.slot.slotNumber, x, y, 14, 14);
		this.slot = slot;
		this.width -= this.marginWidth;
		this.height -= this.marginWidth;
		this.marginWidth = 0;
	}
	
	@Override
	public Style getDefaultStyle()
	{
		return slotStyle;
	}
	
	protected ItemStack getStackToRender()
	{
		return slot.slot.getStack();
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		
		if(isDragged())
			style.getMouseOverBackground(this).renderStyle(helper, width, height);
		
		ItemStack stack = getStackToRender();
		
		ItemStack newStack = null;
		if(isDragged() && stackSize.length > 1){
			updateHandStack();
			newStack = getItemStackByIndex(index);
		}else if(startSlot != null){
			updateStackSize();
			newStack = startSlot.getItemStackByIndex(index);
		}
		
		if(newStack != null)
		{
			if(stack != null)
				newStack.grow(stack.getCount());
			stack = newStack;
		}
		
		if(slot.slot instanceof SlotInput)
		{
			ItemStack backgroundStack = null;
			if(((SlotInput)slot.slot).input != null)
				backgroundStack = ((SlotInput)slot.slot).input.getItemStack(1);
			
			if(backgroundStack != null && !backgroundStack.isEmpty())
			{
				try{
					helper.drawItemStackAndOverlay(backgroundStack, 0, 0, 16, 16);
				}catch(Exception e){	            
		            helper.font.drawString("X", 0, 0, ColorUtils.WHITE);
				}
			}
		}
		
		if(stack != null && !stack.isEmpty())
		{
			try{
				helper.drawItemStackAndOverlay(stack, 0, 0, 16, 16);
			}catch(Exception e){	            
	            helper.font.drawString("X", 0, 0, ColorUtils.WHITE);
			}
		}
	}
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> tips = new ArrayList<String>();
		if(slot.slot.getHasStack() && getPlayer().inventory.getItemStack().isEmpty())
		{
			List list = null;
			try{
				list = slot.slot.getStack().getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips);
		
		        for (int k = 0; k < list.size(); ++k)
		        {
		            if (k == 0)
		            {
		                list.set(k, slot.slot.getStack().getRarity().rarityColor + (String)list.get(k));
		            }
		            else
		            {
		                list.set(k, ChatFormatting.GRAY + (String)list.get(k));
		            }
		            tips.add((String) list.get(k));
		        }
			}catch (Exception e){
				ItemStack stack = slot.slot.getStack();
				try{
					tips.add(Item.REGISTRY.getNameForObject(stack.getItem()).toString());
					tips.add("Damage: " + stack.getItemDamage());
					tips.add("NBT: " + (stack.hasTagCompound() ? "null" : stack.getTagCompound().toString()));
				}catch(Exception e2){
					tips.add("<ERRORED>");
				}
			}
		}
		return tips;
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		if(key == mc.gameSettings.keyBindDrop.getKeyCode() && isMouseOver())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("ctrl", GuiScreen.isCtrlKeyDown());
			nbt.setInteger("type", 2);
			slot.sendPacket(nbt);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		if(!GuiContainer.isShiftKeyDown())
			return false;
		sendClickedPacket(3, scrolled);
		return true;
	}
	
	public void sendClickedPacket(int button, int scrolled)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("button", button);
		boolean shift = GuiScreen.isShiftKeyDown();
		nbt.setBoolean("shift", shift);
		nbt.setInteger("scrolled", scrolled);
		nbt.setInteger("type", 1);
		slot.sendPacket(nbt);
		slot.onSlotClicked(button, shift, scrolled);
	}
	
	public ItemStack dragged = null;
	public boolean isRightClick = false;
	public GuiSlotControl startSlot = null;
	public int[] stackSize = null;
	public int index = -1;
	
	public boolean isDragged()
	{
		return dragged != null;
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button) {
		for (int i = 0; i < getParent().controls.size(); i++) {
			if(getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl) getParent().controls.get(i)).isDragged())
				return true;
		}
		
		ItemStack stack = getPlayer().inventory.getItemStack();
		if(!stack.isEmpty() && button < 2)
		{
			dragged = stack.copy();
			isRightClick = button == 1;
			
			boolean canStack = Container.canAddItemToSlot(slot.slot, dragged, true);
			int stackSize = Math.min(slot.slot.getSlotStackLimit(), dragged.getMaxStackSize());
			if(slot.slot.getHasStack())
				stackSize -= slot.slot.getStack().getCount();
			if(!slot.slot.isItemValid(dragged))
				stackSize = 0;
			if(stackSize > 0 && canStack)
			{
				this.stackSize = new int[1];
				this.stackSize[0] = stackSize;
				this.index = 0;
				return true;
			}else{
				dragged = null;
			}
			
		}
		
		sendClickedPacket(button, 0);
		return true;
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		if(startSlot == null && !isDragged() && isMouseOver(posX, posY))
		{
			for (int i = 0; i < getParent().controls.size(); i++) {
				if(getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl) getParent().controls.get(i)).isDragged())
				{
					startSlot = (GuiSlotControl) getParent().controls.get(i);
					
					boolean canStack = Container.canAddItemToSlot(slot.slot, startSlot.dragged, true);
					int stackSize = Math.min(slot.slot.getSlotStackLimit(), startSlot.dragged.getMaxStackSize());
					if(slot.slot.getHasStack())
						stackSize -= slot.slot.getStack().getCount();
					if(!slot.slot.isItemValid(dragged))
						stackSize = 0;
					if(stackSize > 0 && canStack)
					{
						int[] newStackSize = new int[startSlot.stackSize.length+1];
						for (int j = 0; j < startSlot.stackSize.length; j++)
							newStackSize[j] = startSlot.stackSize[j];
						newStackSize[startSlot.stackSize.length] = stackSize;
						this.index = startSlot.stackSize.length;
						startSlot.stackSize = newStackSize;
					}else{
						startSlot = null;
					}
				}
			}
		}
	}
	
	@Override
	public void mouseReleased(int posX, int posY, int button){
		if(isDragged() && stackSize.length > 1)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			int[] slotArray = new int[stackSize.length];
			slotArray[index] = slot.getID();
			for (int i = 0; i < getParent().controls.size(); i++) {
				if(getParent().controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl) getParent().controls.get(i)).startSlot != null)
				{
					slotArray[((GuiSlotControl) getParent().controls.get(i)).index] = ((GuiSlotControl) getParent().controls.get(i)).slot.getID();
					((GuiSlotControl) getParent().controls.get(i)).startSlot = null;
					((GuiSlotControl) getParent().controls.get(i)).index = -1;
				}
			}
			
			nbt.setIntArray("slots", slotArray);
			nbt.setBoolean("right", isRightClick);
			//dragged.writeToNBT(nbt);
			nbt.setInteger("type", 3);
			slot.sendPacket(nbt);
			slot.splitStack(slotArray, dragged, isRightClick);
		}else if(isDragged()){
			sendClickedPacket(isRightClick ? 1 : 0, 0);
		}
		dragged = null;
		stackSize = null;
		
	}
	
	public void updateStackSize()
	{
		if(startSlot != null)
		{
			int stackSize = Math.min(slot.slot.getSlotStackLimit(), startSlot.dragged.getMaxStackSize());
			if(slot.slot.getHasStack())
				stackSize -= slot.slot.getStack().getCount();
			startSlot.stackSize[index] = stackSize;
		}
	}
	
	public ItemStack getItemStackByIndex(int index)
	{
		if(index >= dragged.getCount())
			return null;
		ItemStack result = dragged.copy();
		if(isRightClick)
			result.setCount(1);
		else{
			int used = 0;
			int StackPerSlot = MathHelper.floor((float)result.getCount() / (float)stackSize.length);
			for (int i = 0; i < index; i++)
				used += Math.min(StackPerSlot, stackSize[i]);
			if(used < result.getCount())
				result.setCount(Math.min(StackPerSlot, stackSize[index]));
		}
		return result;
	}
	
	public void updateHandStack()
	{
		int used = 0;
		int StackPerSlot = MathHelper.floor((float)dragged.getCount() / (float)stackSize.length);
		if(isRightClick)
			StackPerSlot = 1;
		for (int i = 0; i < stackSize.length; i++)
			used += Math.min(StackPerSlot, stackSize[i]);
		int left = dragged.getCount() - used;
		//if(left < 0)
			//left = 0;
		ItemStack hand = dragged.copy();
		hand.setCount(left);
		getPlayer().inventory.setItemStack(hand);
			
	}

	
}
