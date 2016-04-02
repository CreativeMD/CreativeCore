package com.creativemd.creativecore.gui.core;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiControl extends CoreControl{
	
	public static Style defaultStyle = Style.liteStyle;
	
	public int posX;
	public int posY;
	public int width;
	public int height;
	
	public float rotation;
	
	public boolean visible;
	
	protected Style style = null;
	protected int borderWidth = 1;
	protected int marginWidth = 2;
	
	public GuiControl(String name, int x, int y, int width, int height) {
		super(name);
		this.posX = x;
		this.posY = y;
		this.width = width;
		this.height = height;
		this.rotation = 0;
		this.visible = true;
	}
	
	//================Construction================
	
	public GuiControl setRotation(float rotation)
	{
		this.rotation = rotation;
		return this;
	}
	
	public GuiControl setStyle(Style style)
	{
		this.style = style;
		return this;
	}
	
	//================Interaction================
	
	@Override
	public boolean isInteractable()
	{
		return super.isInteractable() && visible;
	}
	
	public boolean isMouseOver()
	{
		/*Vec3d mouse = parent.getMousePos();
		Vec3d pos = getValidPos((int)mouse.xCoord, (int)mouse.yCoord); TODO ADD CALCUTION
		return enabled && parent.isTopLayer() && visible && isMouseOver((int)pos.x, (int)pos.y);*/
		return false;
	}
	
	protected boolean isMouseOver(int posX, int posY)
	{
		return posX >= this.posX && posX < this.posX+this.width && posY >= this.posY && posY < this.posY+this.height;
	}
	
	//================Helper================
	
	public GuiControl getParent()
	{
		return (GuiControl) parent;
	}
	
	//================Styling================
	
	public Style getStyle()
	{
		if(style != null)
			return style;
		if(parent != null)
			return getParent().getStyle();
		return defaultStyle;
	}
	
	//================Tooltip================
	
	public ArrayList<String> getTooltip()
	{
		return new ArrayList<String>();
	}
	
	//================Positioning================
	
	/**
	 * Returns relative positions to (posX/posY)
	 * @return relative positions to (posX/posY)
	 */
	public Vec3d getCenterOffset()
	{
		return new Vec3d(width/2, height/2, 0);
	}
	
	//================Render================
	
	protected abstract void renderContent(GuiRenderHelper helper, Style style, int width, int height);
	
	protected void renderBackground(GuiRenderHelper helper, Style style)
	{
		style.getBorder(this).renderStyle(helper, width, height);
		GlStateManager.translate(borderWidth, borderWidth, 0);
		style.getBackground(this).renderStyle(helper, width-borderWidth*2, height-borderWidth*2);
	}
	
	protected void renderForeground(GuiRenderHelper helper, Style style)
	{
		if(enabled)
			style.getDisableEffect(this).renderStyle(helper, width, height);
	}
	
	protected void renderControl(GuiRenderHelper helper)
	{
		if(!visible)
			return ;
		Style style = getStyle();
		Vec3d centerOffset = getCenterOffset();
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX+centerOffset.xCoord, posY+centerOffset.yCoord, 0);
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.translate(-centerOffset.xCoord, -centerOffset.yCoord, 0);
		
		renderBackground(helper, style);
		
		int spaceUsed = borderWidth+marginWidth;
		
		GlStateManager.translate(marginWidth, marginWidth, 0);
		renderContent(helper, style, width-spaceUsed*2, height-spaceUsed*2);
		
		GlStateManager.translate(-spaceUsed, -spaceUsed, 0);
		renderForeground(helper, style);
		
		GlStateManager.popMatrix();
	}
	
	//================Events================
	
	public boolean mouseScrolled(int posX, int posY, int scrolled){
		return false;
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		return false;
	}
	
	public boolean mouseDragged(int posX, int posY, int button, long time){
		return false;
	}	
	
	public void mouseReleased(int posX, int posY, int button){
		
	}
	
	public void mouseMove(int posX, int posY, int button){
		
	}
	
	public boolean onKeyPressed(char character, int key)
	{
		return false;
	}
	
}
