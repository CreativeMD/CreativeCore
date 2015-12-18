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
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glTranslated(8, 8, 0);
		GL11.glScaled(width/16D, height/16D,0);
		GL11.glTranslated(-width/2D, -height/2D, 0);
		RenderHelper2D.renderItem(stack, width/2, height/2, 1, 1, 16, 16);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}
}
