package com.creativemd.creativecore.common.gui.controls;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.premade.SubContainerControl;
import com.creativemd.creativecore.common.gui.premade.SubGuiControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ForgeHooksClient;

public class GuiScrollBox extends GuiControl{
	
	public SubGuiControl gui;
	public SubContainerControl container;
	
	public int maxScroll = 0;
	public int scrolled = 0;
	public boolean dragged;

	public GuiScrollBox(String name, EntityPlayer player, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		gui = new SubGuiControl(this);
		gui.initGui();
		container = new SubContainerControl(player);
		container.initContainer();
		gui.container = container;
	}
	
	public void addControl(ContainerControl control)
	{
		container.controls.add(control);
		container.refreshControls();
		control.init();
		addControl(control.guiControl);
	}
	
	public void addControl(GuiControl control)
	{
		gui.controls.add(control);
		gui.refreshControls();
		int tempHeight = control.posY + control.height/2 + 10 - this.height;
		if(tempHeight > maxScroll)
			maxScroll = tempHeight;
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		Vector4d black = new Vector4d(0, 0, 0, 255);
		RenderHelper2D.drawGradientRect(0, 0, this.width, this.height, black, black);
		
		Vector4d color = new Vector4d(140, 140, 140, 255);
		RenderHelper2D.drawGradientRect(1, 1, this.width-1, this.height-1, color, color);
		
		ScaledResolution scaledresolution = new ScaledResolution(mc , mc.displayWidth, mc.displayHeight);
		int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        int movex = i/2-parent.width/2+(posX)+1;
        int movey = j/2-parent.height/2+(parent.height-(height+posY))+1;
        int scale = scaledresolution.getScaleFactor();
        movex *= scale;
        movey *= scale;
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(movex,movey,(this.width-2) * scale,(this.height-2) * scale);
		
		GL11.glPushMatrix();
		
		GL11.glTranslated(0, -scrolled, 0);
		gui.renderControls(renderer);
		gui.drawOverlay(renderer);		
		
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		if(isMouseOver())
			gui.renderTooltip(renderer);
		//GL11.glTranslated(0, scrolled, 0);
		
		GL11.glPopMatrix();
		
		
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4d(1, 1, 1, 1);
		color = new Vector4d(130, 130, 130, 255);
		RenderHelper2D.drawGradientRect(this.width-15, 1, this.width-1, this.height-1, color, color);
		
		RenderHelper2D.renderScrollBar(this.width-15, 1, maxScroll == 0 ? 0 : (double)scrolled/(double)maxScroll, this.height-2, maxScroll <= 0);
		
	}
	
	public void onScrolled()
	{
		if(this.scrolled < 0)
			this.scrolled = 0;
		if(this.scrolled > maxScroll)
			this.scrolled = maxScroll;
		gui.scrolled = scrolled;
	}
	
	@Override
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		if(!gui.mouseScrolled(posX, posY, scrolled))
		{
			this.scrolled -= scrolled*10;
		}
		onScrolled();
		return true;
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button){
		gui.mousePressed(posX, posY, button);
		return true;
	}
	
	@Override
	public boolean mouseDragged(int posX, int posY, int button, long time){
		gui.mouseDragged(posX, posY, button, time);
		if(width-posX <= 14)
			dragged = true;
		return true;
	}	
	
	@Override
	public void mouseReleased(int posX, int posY, int button){
		gui.mouseReleased(posX, posY, button);
		dragged = false;
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		gui.mouseMove(posX, posY, button);
		if(dragged)
		{
			double percent = (double)(posY-(this.posY))/(double)(height);
			scrolled = (int) (percent*maxScroll);
			onScrolled();
		}
		
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		return gui.keyTyped(character, key);
	}
}
