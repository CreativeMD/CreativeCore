package com.creativemd.creativecore.common.gui.controls;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.premade.SubGuiControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.ForgeHooksClient;

public class GuiScrollBox extends GuiControl{
	
	public SubGuiControl gui;

	public GuiScrollBox(int x, int y, int width, int height) {
		super(x, y, width, height);
		gui = new SubGuiControl(this);
		gui.initGui();
	}
	
	public void addControl(GuiControl control)
	{
		gui.controls.add(control);
		gui.refreshControls();
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d color = new Vector4d(140, 140, 140, 255);
		//GL11.glColorMask(false, false, false, false); 
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, color, color);
		
		ScaledResolution scaledresolution = new ScaledResolution(mc , mc.displayWidth, mc.displayHeight);
		int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int movex = i/2-parent.width/2+(posX-width/2)+1;
        int movey = j/2-parent.height/2+(posX-height/2)+1;
        int scale = scaledresolution.getScaleFactor();
        movex *= scale;
        movey *= scale;
        //movex = (int)parent.getMousePos().x;
        //movey = (int)parent.getMousePos().y;
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(movex,movey,(this.width-2) *scale,(this.height-2) * scale);
		
		gui.drawForeground(renderer);
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		return gui.mouseScrolled(posX, posY, scrolled);
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button){
		gui.mousePressed(posX, posY, button);
		return true;
	}
	
	@Override
	public boolean mouseDragged(int posX, int posY, int button, long time){
		gui.mouseDragged(posX, posY, button, time);
		return true;
	}	
	
	@Override
	public void mouseReleased(int posX, int posY, int button){
		gui.mouseReleased(posX, posY, button);
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		gui.mouseMove(posX, posY, button);
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		return gui.keyTyped(character, key);
	}
}
