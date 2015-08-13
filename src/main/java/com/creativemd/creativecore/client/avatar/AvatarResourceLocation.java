package com.creativemd.creativecore.client.avatar;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class AvatarResourceLocation extends Avatar{
	
	public ResourceLocation resource;
	
	public AvatarResourceLocation(ResourceLocation resource)
	{
		this.resource = resource;
	}
	
	@Override
	public void handleRendering(Minecraft mc, FontRenderer fontRenderer, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        mc.getTextureManager().bindTexture(resource);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper2D.drawTexturedModalRect(0, 0, 0, 0, width, height);
	}

}
