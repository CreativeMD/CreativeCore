package com.creativemd.creativecore.gui;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiControl extends CoreControl{
	
	public static Minecraft mc = Minecraft.getMinecraft();
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
		this.width = width+getContentOffset()*2;
		this.height = height+getContentOffset()*2;
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
	
	public Vec3d getMousePos()
	{
		return getParent().getMousePos();
	}
	
	public Vec3d rotateMouseVec(Vec3d mouse)
	{
		Vec3d centerOffset = getCenterOffset();
		return getRotationAround(-rotation, mouse, new Vec3d(posX+centerOffset.xCoord, posY+centerOffset.yCoord, 0));
	}
	
	public boolean hasMouseOverEffect()
	{
		return true;
	}
	
	public boolean isMouseOver()
	{
		Vec3d mouse = getParent().getMousePos();
		Vec3d pos = rotateMouseVec(mouse);
		return isInteractable() && visible && isMouseOver((int)pos.xCoord, (int)pos.yCoord);
	}
	
	public boolean isMouseOver(int posX, int posY)
	{
		return posX >= this.posX && posX < this.posX+this.width && posY >= this.posY && posY < this.posY+this.height;
	}
	
	//================Helper================
	
	public GuiParent getParent()
	{
		return (GuiParent) parent;
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
	
	public boolean hasBorder()
	{
		return true;
	}
	
	public boolean hasBackground()
	{
		return true;
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
	
	public int getContentOffset()
	{
		return borderWidth + marginWidth;
	}
	
	public boolean isVisibleInsideRect(int x, int y, int width, int height, float scale)
	{
		return (this.posX+this.width)*scale >= x*scale && this.posX*scale <= x*scale+width && (this.posY+this.height)*scale >= y*scale && this.posY*scale <= y*scale+height;
	}
	
	//================Render================
	
	protected abstract void renderContent(GuiRenderHelper helper, Style style, int width, int height);
	
	protected void renderBackground(GuiRenderHelper helper, Style style)
	{
		if(hasBorder())
			style.getBorder(this).renderStyle(helper, width, height);
		GlStateManager.translate(borderWidth, borderWidth, 0);
		if(hasBackground())
			style.getBackground(this).renderStyle(helper, width-borderWidth*2, height-borderWidth*2);
	}
	
	protected void renderForeground(GuiRenderHelper helper, Style style)
	{
		if(!enabled)
			style.getDisableEffect(this).renderStyle(helper, width, height);
	}
	
	protected void prepareStencil(int x, int y, float scale, GuiRenderHelper helper, int maxWidth, int maxHeight)
	{
		int spaceUsed = borderWidth+marginWidth;
		
		//GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		//GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); 		
		
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
		
		System.out.println("Stencil at: " + x + " " + y + " " + maxWidth + " " + maxHeight + " name=" + this.name);
		//maxWidth += x;
		//maxHeight += y;
		int minX = 0;
		if(x < 0)
			minX = -x;
		int minY = 0;
		if(y < 0)
			minY = -y;
		int maxX = Math.min(maxWidth, width-spaceUsed*2);
		int maxY = Math.min(maxHeight, height-spaceUsed*2);
		System.out.println("Rendering at: " + minX + " " + minY + " " + maxX + " " + maxY + " name=" + this.name);
		
		GL11.glColorMask(false, false, false, false);
		helper.renderColorPlate(minX, minY, new Color(0, 0, 0, 255), maxX, maxY);
		GL11.glColorMask(true, true, true, true);
	}
	
	public void renderControl(GuiRenderHelper helper, int maxWidth, int maxHeight)
	{
		renderControl(helper, 0, 0, 1, maxWidth, maxHeight);
	}
	
	public void renderControl(GuiRenderHelper helper, int x, int y, float scale, int maxWidth, int maxHeight)
	{
		if(!visible)
			return ;
		Style style = getStyle();
		Vec3d centerOffset = getCenterOffset();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX+centerOffset.xCoord, posY+centerOffset.yCoord, 0);
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.translate(-centerOffset.xCoord, -centerOffset.yCoord, 0);
		
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, 0);
		
		renderBackground(helper, style);
		
		int spaceUsed = getContentOffset();
		
		GlStateManager.translate(marginWidth, marginWidth, 0);
		
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		prepareStencil(x, y, scale, helper, maxWidth, maxHeight);
		
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
		
		renderContent(helper, style, Math.min(maxWidth, width-spaceUsed*2), Math.min(maxHeight, height-spaceUsed*2));
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		
		GlStateManager.translate(-spaceUsed, -spaceUsed, 0);
		renderForeground(helper, style);
		
		GlStateManager.popMatrix();
	}
	
	//================Events================
	
	public void onLoseFocus() {}
	
	public boolean mouseScrolled(int x, int y, int scrolled)
	{
		return false;
	}
	
	public boolean mousePressed(int x, int y, int button)
	{
		return false;
	}
	
	public void mouseMove(int x, int y, int button) {}
	
	public void mouseReleased(int x, int y, int button) {}
	
	public void mouseDragged(int x, int y, int button, long time){}
	
	public boolean onKeyPressed(char character, int key)
	{
		return false;
	}
	
	//================STATIC HELPERS================
	
	public static void playSound(SoundEvent event)
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(event, 1.0F));
	}
	
	public static Vec3d getRotationAround(double angle, Vec3d pos, Vec3d center)
	{
		Vector2d result = new Vector2d(pos.xCoord, pos.yCoord);
		Vector2d newCenter = new Vector2d(center.xCoord, center.yCoord);
		result.sub(newCenter);
		Vector2d temp = new Vector2d(result);
		result.x = Math.cos(Math.toRadians(angle)) * temp.x - Math.sin(Math.toRadians(angle)) * temp.y;
		result.y = Math.sin(Math.toRadians(angle)) * temp.x + Math.cos(Math.toRadians(angle)) * temp.y;
		result.add(newCenter);
		return new Vec3d(result.x, result.y, 0);
	}
}
