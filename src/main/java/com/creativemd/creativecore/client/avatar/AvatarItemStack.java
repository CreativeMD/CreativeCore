package com.creativemd.creativecore.client.avatar;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class AvatarItemStack extends Avatar{
	
	public ItemStack stack;
	
	public AvatarItemStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public void handleRendering(Minecraft mc, FontRenderer fontRenderer, int width, int height) {
		RenderHelper2D.renderItem(stack, width/2-16/2, height/2-16/2);
	}
}
