package com.creativemd.creativecore.common.gui.controls.container;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.slot.SlotControl;
import com.creativemd.creativecore.common.container.slot.SlotOutput;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiControl;

public class GuiSlotControl extends GuiControl{
	
	public SlotControl slot;
	
	public GuiSlotControl(int x, int y, SlotControl slot) {
		super(x, y, 18, 18);
		this.slot = slot;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GuiContainerSub.background);
		RenderHelper2D.drawTexturedModalRect(0, 0, 176, 0, 18, 18);
		
		ItemStack stack = slot.slot.getStack();
		
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
				newStack.stackSize += stack.stackSize;
			stack = newStack;
		}
		
		if(stack != null)
		{
			try
			{
				GL11.glTranslated(8, 8, 0);
				GL11.glRotated(-rotation, 0, 0, 1);
				GL11.glTranslated(-8, -8, 0);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glEnable(GL11.GL_DEPTH_TEST);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            RenderHelper.enableGUIStandardItemLighting();
				//GL11.glPushMatrix();
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		        
				GL11.glEnable(GL11.GL_LIGHTING);
				//GL11.glEnable(GL11.GL_LIGHTING);
				//RenderHelper.enableStandardItemLighting();
				//GL11.glTranslatef(0.0F, 0.0F, 32.0F);
				SubGui.itemRender.zLevel = 200.0F;
				FontRenderer font = null;
				if (slot.slot.getStack() != null)
					font = slot.slot.getStack().getItem().getFontRenderer(stack);
				if(font == null)
					font = renderer;
				SubGui.itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, 1, 1);
				String textStack = null;
				if (stack.stackSize == 0)
				{
					textStack = "" + EnumChatFormatting.YELLOW + "0";
				}
				SubGui.itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, 1, 1, textStack);
				SubGui.itemRender.zLevel = 0.0F;
			}catch(Exception e){
				SubGui.itemRender.zLevel = 100.0F;
	            
	        	ResourceLocation resourcelocation = mc.renderEngine.getResourceLocation(1);
	            IIcon icon = ((TextureMap)mc.getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
	            Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
	            
	        	SubGui.itemRender.renderIcon(1, 1, icon, 16, 16);

	        	SubGui.itemRender.zLevel = 0.0F;
			}
		}
		
		if(isMouseOver() || startSlot != null || isDragged())
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glColorMask(true, true, true, false);
			RenderHelper2D.drawGradientRect(1, 1, 17, 17, -2130706433, -2130706433);
			GL11.glColorMask(true, true, true, true);
			//GL11.glEnable(GL11.GL_LIGHTING);
			//GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
        //RenderHelper.enableStandardItemLighting();
	}
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> tips = new ArrayList<String>();
		if(slot.slot.getHasStack() && parent.container.player.inventory.getItemStack() == null)
		{
			List list = slot.slot.getStack().getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
	
	        for (int k = 0; k < list.size(); ++k)
	        {
	            if (k == 0)
	            {
	                list.set(k, slot.slot.getStack().getRarity().rarityColor + (String)list.get(k));
	            }
	            else
	            {
	                list.set(k, EnumChatFormatting.GRAY + (String)list.get(k));
	            }
	            tips.add((String) list.get(k));
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
			slot.sendNBTUpdate(2, nbt);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
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
		slot.sendNBTUpdate(1, nbt);
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
	public boolean mousePressed(int posX, int posY, int button){
		ItemStack stack = parent.container.player.inventory.getItemStack();
		if(stack != null && button < 2)
		{
			dragged = stack.copy();
			isRightClick = button == 1;
			
			boolean canStack = Container.func_94527_a(slot.slot, dragged, true);
			int stackSize = Math.min(slot.slot.getSlotStackLimit(), dragged.getMaxStackSize());
			if(slot.slot.getHasStack())
				stackSize -= slot.slot.getStack().stackSize;
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
	public boolean mouseDragged(int posX, int posY, int button){
		if(startSlot == null && !isDragged())
		{
			for (int i = 0; i < parent.controls.size(); i++) {
				if(parent.controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl) parent.controls.get(i)).isDragged())
				{
					startSlot = (GuiSlotControl) parent.controls.get(i);
					
					boolean canStack = Container.func_94527_a(slot.slot, startSlot.dragged, true);
					int stackSize = Math.min(slot.slot.getSlotStackLimit(), startSlot.dragged.getMaxStackSize());
					if(slot.slot.getHasStack())
						stackSize -= slot.slot.getStack().stackSize;
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
		return true;
	}
	
	/*@Override
	public void mouseMove(int posX, int posY, int button){
		
	}*/
	
	@Override
	public void mouseReleased(int posX, int posY, int button){
		if(isDragged() && stackSize.length > 1)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			int[] slotArray = new int[stackSize.length];
			slotArray[index] = slot.getID();
			for (int i = 0; i < parent.controls.size(); i++) {
				if(parent.controls.get(i) instanceof GuiSlotControl && ((GuiSlotControl) parent.controls.get(i)).startSlot != null)
				{
					slotArray[((GuiSlotControl) parent.controls.get(i)).index] = ((GuiSlotControl) parent.controls.get(i)).slot.getID();
					((GuiSlotControl) parent.controls.get(i)).startSlot = null;
					((GuiSlotControl) parent.controls.get(i)).index = -1;
				}
			}
			
			nbt.setIntArray("slots", slotArray);
			nbt.setBoolean("right", isRightClick);
			dragged.writeToNBT(nbt);
			slot.sendNBTUpdate(3, nbt);
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
				stackSize -= slot.slot.getStack().stackSize;
			startSlot.stackSize[index] = stackSize;
		}
	}
	
	public ItemStack getItemStackByIndex(int index)
	{
		if(index >= dragged.stackSize)
			return null;
		ItemStack result = dragged.copy();
		if(isRightClick)
			result.stackSize = 1;
		else{
			int used = 0;
			int StackPerSlot = MathHelper.floor_float((float)result.stackSize / (float)stackSize.length);
			for (int i = 0; i < index; i++)
				used += Math.min(StackPerSlot, stackSize[i]);
			if(used < result.stackSize)
				result.stackSize = Math.min(StackPerSlot, stackSize[index]);
		}
		return result;
	}
	
	public void updateHandStack()
	{
		int used = 0;
		int StackPerSlot = MathHelper.floor_float((float)dragged.stackSize / (float)stackSize.length);
		if(isRightClick)
			StackPerSlot = 1;
		for (int i = 0; i < stackSize.length; i++)
			used += Math.min(StackPerSlot, stackSize[i]);
		int left = dragged.stackSize - used;
		//if(left < 0)
			//left = 0;
		ItemStack hand = parent.container.player.inventory.getItemStack().copy();
		hand.stackSize = left;
		parent.container.player.inventory.setItemStack(hand);
			
	}
}
