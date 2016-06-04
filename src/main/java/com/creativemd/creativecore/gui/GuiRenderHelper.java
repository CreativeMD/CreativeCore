package com.creativemd.creativecore.gui;

import java.util.ArrayList;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRenderHelper {
	
	public static GuiRenderHelper instance = new GuiRenderHelper(Minecraft.getMinecraft().fontRendererObj);
	
	public FontRenderer font;
	
	public GuiRenderHelper(FontRenderer font)
	{
		this.font = font;
	}
	
	public void renderColorPlate(Color color, int width, int height)
	{
		Gui.drawRect(0, 0, width, height, ColorUtils.RGBAToInt(color));
	}
	
	public int getFontHeight()
	{
		return font.FONT_HEIGHT;
	}
	
	public int getStringWidth(String text)
	{
		return font.getStringWidth(text);
	}

	public void drawTooltip(ArrayList<String> tooltip, int xCoord, int yCoord, GuiControl subGui) {
		// TODO Auto-generated method stub
		
	}
	
}
