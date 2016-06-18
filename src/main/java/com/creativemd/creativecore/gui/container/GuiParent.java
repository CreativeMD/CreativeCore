package com.creativemd.creativecore.gui.container;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.Rect;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.controls.gui.GuiAvatarButton;
import com.creativemd.creativecore.gui.event.ControlEvent;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.gui.event.gui.GuiToolTipEvent;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

public abstract class GuiParent extends GuiControl implements IControlParent {
	
	public ArrayList<GuiControl> controls = new ArrayList<>();
	
	public GuiParent(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}

	@Override
	public List getControls() {
		return controls;
	}

	@Override
	public void refreshControls() {
		for (int i = 0; i < controls.size(); i++)
		{
			updateControl(controls.get(i), i);
		}
	}
	
	public void updateControl(GuiControl control, int id)
	{
		control.parent = this;
		control.setID(id);
	}
	
	public void addControl(GuiControl control) {
		updateControl(control, controls.size());
		controls.add(control);
	}
	
	//================Rendering================
	
	protected int lastRenderedHeight = 0;
	
	public float getScaleFactor()
	{
		return 1F;
	}
	
	protected int getOffsetY()
	{
		return 0;
	}
	
	protected int getOffsetX()
	{
		return 0;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height, Rect relativeMaximumRect)
	{
		float scale = getScaleFactor();
		int xOffset = getOffsetX();
		int yOffset = getOffsetY();
		
		Rect newRect = relativeMaximumRect.mergeRects(getRect());
		
		lastRenderedHeight = 0;
		
		for (int i = controls.size()-1; i >= 0; i--) {
			GuiControl control = controls.get(i);
			
			if(control.visible && control.isVisibleInsideRect(-xOffset, -yOffset, width, height, scale))
			{
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				
				prepareContentStencil(helper, relativeMaximumRect);
				
				GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
				GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(xOffset, yOffset, 0);				
				control.renderControl(helper, scale, newRect.getOffsetRect(xOffset, yOffset));
				GlStateManager.popMatrix();
				
				GL11.glDisable(GL11.GL_STENCIL_TEST);
			}
			
			lastRenderedHeight = (int) Math.max(lastRenderedHeight, (control.posY+control.height)*scale);
			
		}
		renderContent(helper, style, width, height);
	}
	
	//================Helper================
	
	@Override
	public CoreControl get(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
    		GuiControl control = controls.get(i);
    		if(control.is(name))
				return control;
			if(control instanceof IControlParent)
			{
				CoreControl tempcontrol = ((IControlParent) control).get(name);
				if(tempcontrol != null)
					return tempcontrol;
			}
		}
    	return null;
    }
    
	@Override
    public boolean has(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
    		GuiControl control = controls.get(i);
			if(control.name.equalsIgnoreCase(name))
				return true;
		}
    	return false;
    }
	
	//================Interaction================
	
	public boolean isMouseOver()
	{
		if(parent != null)
			return super.isMouseOver();
		return true;
	}
	
	@Override
	public Vec3d getMousePos()
	{
		if(parent != null)
			return getParent().getMousePos().addVector(-getOffsetX()-this.posX, -getOffsetY()-this.posY, 0);
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
		int x = Mouse.getEventX() * i / mc.displayWidth;
        int y = j - Mouse.getEventY() * j / mc.displayHeight - 1;
        int movex = (i - width)/2;
        int movey = (j - height)/2;
        x -= movex;
        y -= movey;
		return new Vec3d(x-getContentOffset()-getOffsetX(), y-getContentOffset()-getOffsetY(), 0);
	}
	
	//================Custom Events================
	
	@Override
	public void onOpened()
    {
    	for (int i = 0; i < controls.size(); i++) {
    		GuiControl control = controls.get(i);
    		control.parent = this;
    		control.onOpened();
    	}
		refreshControls();
    }
	
	@Override
	public void onClosed()
	{
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onClosed();
		}
		//eventBus.removeAllEventListeners();
	}
	
	@Override
	public void onTick()
	{
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onTick();
		}
	}
	
	//================Events================
	
	@Override
	public void onLoseFocus()
	{
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onLoseFocus();
		}
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled)
	{
		Vec3d mouse = getMousePos();
		for(int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);			
			if(control.isInteractable() && control.isMouseOver((int)pos.xCoord, (int)pos.yCoord) && control.mouseScrolled((int)pos.xCoord, (int)pos.yCoord, scrolled))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button)
	{
		boolean result = false;
		Vec3d mouse = getMousePos();
		for(int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);			
			if(!result && control.isInteractable() && control.isMouseOver((int)pos.xCoord, (int)pos.yCoord) && control.mousePressed((int)pos.xCoord, (int)pos.yCoord, button)){
				raiseEvent(new GuiControlClickEvent(control, x, y));
				result = true;
			}else
				control.onLoseFocus();
		}
		return result;
	}
	
	@Override
	public void mouseMove(int x, int y, int button)
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);			
			if(control.isInteractable())
				control.mouseMove((int)pos.xCoord, (int)pos.yCoord, button);
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);			
			if(control.isInteractable())
				control.mouseReleased((int)pos.xCoord, (int)pos.yCoord, button);
		}
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		for (int i = 0; i < controls.size(); i++) {	
			GuiControl control = controls.get(i);
			if(control.isInteractable() && control.onKeyPressed(character, key))
				return true;
		}
		return false;
	}
	
	
	//================Tooltip================
	
	public GuiToolTipEvent getToolTipEvent()
	{
		GuiToolTipEvent event = super.getToolTipEvent();
		if(event != null)
			return event;
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);			
			if(control.isInteractable() && control.isMouseOver((int)pos.xCoord, (int)pos.yCoord))
			{
				event = control.getToolTipEvent();
				if(event != null)
					return event;
			}
		}
		return null;
	}

}
