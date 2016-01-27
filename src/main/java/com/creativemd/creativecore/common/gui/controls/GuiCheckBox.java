package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector4d;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.gui.FontRenderer;

public class GuiCheckBox extends GuiControl{
	
	public static final int checkBoxWidth = 7;
	
	public boolean value = false;
	public String title;
	
	public GuiCheckBox(String name, String title, int x, int y, boolean value) {
		super(name, x, y, checkBoxWidth + mc.fontRenderer.getStringWidth(title) + 3, 15);
		this.value = value;
		this.title = title;
	}
	
	public GuiCheckBox(String name, int x, int y, boolean value) {
		this(name, name, x, y, value);
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		
		int yoffset = 3;
		
		Vector4d black = new Vector4d(0, 0, 0, 255);
		
		//RenderHelper2D.drawGradientRect(0, 0, width, height, black, black);
		
		RenderHelper2D.drawGradientRect(0, yoffset, checkBoxWidth, yoffset+checkBoxWidth, black, black);
		
		Vector4d color = new Vector4d(140, 140, 140, 255);
		RenderHelper2D.drawGradientRect(1, yoffset+1, checkBoxWidth-1, yoffset+checkBoxWidth-1, color, color);
		
		if(value)
			renderer.drawString("x", 1, yoffset-1, White);
		
		renderer.drawStringWithShadow(title, checkBoxWidth+3, 3, White);
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button){
		playSound("gui.button.press");
		this.value = !value;
		return true;
	}
}
