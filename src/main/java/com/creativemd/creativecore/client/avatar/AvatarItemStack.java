package com.creativemd.creativecore.client.avatar;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.gui.GuiRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AvatarItemStack extends Avatar{
	
	public ItemStack stack;
	
	public AvatarItemStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public void handleRendering(GuiRenderHelper helper, int width, int height) {
		helper.drawItemStack(stack, 0, 0, width, height);
	}
}
