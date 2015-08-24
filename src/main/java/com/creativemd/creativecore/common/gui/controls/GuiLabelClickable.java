package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector4d;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class GuiLabelClickable extends GuiLabel{

	public GuiLabelClickable(String title, int x, int y, int color)
	{
		super(title, x, y, color);
		
	}
	
	public GuiLabelClickable(String title, int x, int y)
	{
		super(title, x, y);
	}
	
	public GuiLabelClickable(FontRenderer font, String title, int x, int y, int color)
	{
		super(font, title, x, y, color);
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		return true;
	}
	
	@Override
	public void drawControl(FontRenderer renderer) {
		if(isMouseOver())
		{
			Vector4d black = new Vector4d(0, 0, 0, 255);
			RenderHelper2D.drawGradientRect(-1, -1, this.width+1, 0, black, black);
			RenderHelper2D.drawGradientRect(-1, 0, 0, this.height, black, black);
			RenderHelper2D.drawGradientRect(this.width, 0, this.width+1, this.height, black, black);
			RenderHelper2D.drawGradientRect(-1, this.height-1, this.width+1, this.height, black, black);
		}
		super.drawControl(renderer);
	}
	
	@Override
	public int getColor()
	{
		if(isMouseOver())
			return 16777120;
		return super.getColor();
	}
		

}
