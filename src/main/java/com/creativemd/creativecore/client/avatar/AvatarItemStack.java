package com.creativemd.creativecore.client.avatar;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.SubGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class AvatarItemStack extends Avatar{
	
	public ItemStack stack;
	
	public AvatarItemStack(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public void handleRendering(Minecraft mc, FontRenderer fontRenderer, int width, int height) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper2D.renderItem(stack, width/2-16/2, height/2-16/2);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
