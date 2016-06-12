package com.creativemd.creativecore.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRenderHelper {
	
	public static GuiRenderHelper instance = new GuiRenderHelper(Minecraft.getMinecraft());
	
	public FontRenderer font;
	public RenderItem itemRenderer;
	
	public GuiRenderHelper(Minecraft mc)
	{
		this(mc.fontRendererObj, mc.getRenderItem());
	}
	
	public GuiRenderHelper(FontRenderer font, RenderItem itemRenderer)
	{
		this.font = font;
		this.itemRenderer = itemRenderer;
	}
	
	public void renderColorPlate(Color color, int width, int height)
	{
		renderColorPlate(0, 0, color, width, height);
	}
	
	public void renderColorPlate(int x, int y, Color color, int width, int height)
	{
		Gui.drawRect(x, y, width, height, ColorUtils.RGBAToInt(color));
	}
	
	public int getFontHeight()
	{
		return font.FONT_HEIGHT;
	}
	
	public int getStringWidth(String text)
	{
		return font.getStringWidth(text);
	}
	
	public int drawStringWithShadow(String text, int width, int height, int color)
	{
		return drawStringWithShadow(text, 0, 0, width, height, color);
	}
	
	public int drawStringWithShadow(String text, int x, int y, int width, int height, int color)
	{
		return drawStringWithShadow(text, x, y, width, height, color, 0);
	}
	
	public int drawStringWithShadow(String text, int x, int y, int width, int height, int color, int additionalWidth)
	{
		int completeWidth = font.getStringWidth(text)+additionalWidth;
		font.drawStringWithShadow(text, width/2-completeWidth/2+additionalWidth, height/2-getFontHeight()/2, color);
		return completeWidth;
	}

	public void drawTooltip(ArrayList<String> tooltip, int xCoord, int yCoord, GuiControl subGui) {
		// TODO Auto-generated method stub
		
	}
	
	public void drawItemStack(ItemStack stack, int x, int y, int width, int height)
	{
		GlStateManager.pushMatrix();
		GlStateManager.scale(width/16D, height/16D,0);
		itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.popMatrix();
	}
}
