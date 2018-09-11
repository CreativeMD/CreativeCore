package com.creativemd.creativecore.gui;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;
import com.creativemd.creativecore.gui.event.gui.GuiToolTipEvent;

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
public abstract class GuiControl extends CoreControl {
	
	public static final ResourceLocation guiUtilsImage = new ResourceLocation(CreativeCore.modid, "textures/gui/utils.png");
	public static Minecraft mc = Minecraft.getMinecraft();
	public static FontRenderer font = mc.fontRenderer;
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
	
	protected ArrayList<String> customTooltip = null;
	
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
	
	public GuiControl setDimension(int width, int height)
	{
		this.width = width+getContentOffset()*2;
		this.height = height+getContentOffset()*2;
		return this;
	}
	
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
	
	public GuiControl setCustomTooltip(String... lines)
	{
		this.customTooltip = new ArrayList<>(Arrays.asList(lines));
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
		if(rotation == 0)
			return mouse;
		Vec3d centerOffset = getCenterOffset();
		return getRotationAround(-rotation, mouse, new Vec3d(posX+centerOffset.x, posY+centerOffset.y, 0));
	}
	
	public boolean isMouseOver()
	{
		Vec3d mouse = getParent().getMousePos();
		Vec3d pos = rotateMouseVec(mouse);
		return isInteractable() && visible && isMouseOver((int)pos.x, (int)pos.y);
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
	
	public Style getDefaultStyle()
	{
		return defaultStyle;
	}
	
	public boolean hasStyle()
	{
		if(parent != null && getParent().hasStyle())
			return true;
		return style != null;
	}
	
	public Style getStyle()
	{
		if(style != null)
			return style;
		if(parent != null && hasStyle())
			return getParent().getStyle();
		return getDefaultStyle();
	}
	
	public boolean hasBorder()
	{
		return true;
	}
	
	public boolean hasBackground()
	{
		return true;
	}
	
	public boolean hasMouseOverEffect()
	{
		return true;
	}
	
	public boolean canOverlap()
	{
		return false;
	}
	
	//================Tooltip================
	
	public GuiToolTipEvent getToolTipEvent()
	{
		ArrayList<String> toolTip = getTooltip();
		if(customTooltip != null)
			if(toolTip == null)
				toolTip = new ArrayList<>(customTooltip);
			else
				toolTip.addAll(customTooltip);
		if(toolTip != null && toolTip.size() > 0)
			return new GuiToolTipEvent(toolTip, this);
		return null;
	}
	
	public ArrayList<String> getTooltip()
	{
		return null;
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
	
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height, Rect relativeMaximumRect)
	{
		renderContent(helper, style, width, height);
	}
	
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
	
	protected Rect getRect()
	{
		return new Rect(0, 0, this.width-getContentOffset()*2, this.height-getContentOffset()*2);
	}
	
	protected void prepareContentStencil(GuiRenderHelper helper, Rect maximumRect)
	{
		int spaceUsed = borderWidth+marginWidth;
		
		Rect contentRect = getRect();
		
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
		
		GL11.glColorMask(false, false, false, false);
		contentRect.mergeRects(maximumRect).renderRect(helper, new Color(0, 0, 0, 255));
		GL11.glColorMask(true, true, true, true);
	}
	
	public void renderControl(GuiRenderHelper helper, float scale, Rect relativeMaximumRect)
	{
		if(!visible)
			return ;
		Style style = getStyle();
		
		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(posX, posY, 0);
		if(rotation != 0)
		{
			Vec3d centerOffset = getCenterOffset();
			GlStateManager.translate(centerOffset.x, centerOffset.y, 0);
			GlStateManager.rotate(rotation, 0, 0, 1);
			GlStateManager.translate(-centerOffset.x, -centerOffset.y, 0);
		}
		
		if(scale != 0)
			GlStateManager.scale(scale, scale, 1);
		
		renderBackground(helper, style);
		
		int spaceUsed = getContentOffset();
		
		GlStateManager.translate(marginWidth, marginWidth, 0);
		
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		//prepareContentStencil(helper, relativeMaximumRect);
		
		//prepareStencil(x, y, scale, helper, maxWidth, maxHeight);
		
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
		
		renderContent(helper, style, width-spaceUsed*2, height-spaceUsed*2, relativeMaximumRect);
		
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
	
	private static final Rect screenRect = new Rect(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	
	public static Rect getScreenRect()
	{
		return screenRect;
	}
	
	public static void playSound(SoundEvent event)
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(event, 1.0F));
	}
	
	public static Vec3d getRotationAround(double angle, Vec3d pos, Vec3d center)
	{
		Vector2d result = new Vector2d(pos.x, pos.y);
		Vector2d newCenter = new Vector2d(center.x, center.y);
		result.sub(newCenter);
		Vector2d temp = new Vector2d(result);
		result.x = Math.cos(Math.toRadians(angle)) * temp.x - Math.sin(Math.toRadians(angle)) * temp.y;
		result.y = Math.sin(Math.toRadians(angle)) * temp.x + Math.cos(Math.toRadians(angle)) * temp.y;
		result.add(newCenter);
		return new Vec3d(result.x, result.y, 0);
	}
}
