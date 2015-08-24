package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;

import javax.vecmath.Vector4d;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class GuiComboBox extends GuiControl{
	
	public GuiComboBoxExtension extension;
	public ArrayList<String> lines;
	public String caption;
	
	public GuiComboBox(String name, int x, int y, int width, ArrayList<String> lines) {
		super(name, x, y, width, 20);
		if(lines.size() > 0)
			this.caption = lines.get(0);
		else
			this.caption = "";
		this.lines = lines;
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d color = new Vector4d(60, 60, 60, 255);
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, color, color);
		
		renderer.drawString(caption, 4, height/2-renderer.FONT_HEIGHT/2, 14737632);
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		if(extension == null)
			openBox();
		else
			closeBox();
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		return true;
	}
	
	public void openBox()
	{
		extension = new GuiComboBoxExtension(name + "extension", parent.container.player, this, posX, posY+height, width, 100, lines);
		parent.controls.add(extension);
		
		extension.parent = parent;
		extension.moveControlToBottom();
		extension.init();
		parent.refreshControls();
		extension.rotation = rotation;
	}
	
	public void closeBox()
	{
		parent.removeControl(extension);
		extension = null;
	}
}
