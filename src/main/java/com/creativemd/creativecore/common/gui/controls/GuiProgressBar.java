package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.GuiContainerSub;

public class GuiProgressBar extends GuiControl{
	
	public GuiProgressBar(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public GuiProgressBar(int x, int y, int width, int height, int rotation) {
		super(x, y, width, height, rotation);
	}
	
	public double pos;
	public double max;
	
	public double getPercent()
	{
		return pos/max;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		int border = 1;
		GL11.glDisable(GL11.GL_LIGHTING);
		RenderHelper2D.drawRect(0, 0, 0+width, 0+height, Vec3.createVectorHelper(0, 0, 0), 1);
		RenderHelper2D.drawRect(0+border, 0+border, 0+width-border, 0+height-border, Vec3.createVectorHelper(1, 1, 1), 1);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiContainerSub.background);
		double scale = (double)(height-border*2)/8D;
		GL11.glScaled(1, scale, 1);
		double percent = getPercent();
		RenderHelper2D.drawTexturedModalRect(1, 1D-(scale)/5D, 1, 167, percent*(double)(width-border-1), 8);
	}

}
