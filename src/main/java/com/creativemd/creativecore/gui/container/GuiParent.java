package com.creativemd.creativecore.gui.container;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.event.ControlEvent;

import net.minecraft.client.gui.ScaledResolution;
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
			controls.get(i).parent = this;
			controls.get(i).setID(i);
		}
	}

	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		for (int i = controls.size()-1; i >= 0; i--) {
			GuiControl control = controls.get(i);
			
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			
			resetStencil(helper);
			
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
			
			if(control.visible && control.posY+control.height >= 0 && control.posY <= height)
				control.renderControl(helper);
			
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			
		}
	}
	
	//================Helper================
	
	@Override
	public CoreControl get(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).name.equalsIgnoreCase(name))
				return controls.get(i);
		}
    	return null;
    }
    
	@Override
    public boolean has(String name)
    {
    	for (int i = 0; i < controls.size(); i++) {
			if(controls.get(i).name.equalsIgnoreCase(name))
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
			return getParent().getMousePos();
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
		int x = Mouse.getEventX() * i / mc.displayWidth;
        int y = j - Mouse.getEventY() * j / mc.displayHeight - 1;
        int movex = (i - width)/2;
        int movey = (j - height)/2;
        x -= movex;
        y -= movey;
		return new Vec3d(x-getContentOffset(), y-getContentOffset(), 0);
	}
	
	//================Custom Events================
	
	@Override
	public void onOpened()
    {
    	for (int i = 0; i < controls.size(); i++) {
    		controls.get(i).parent = this;
    		controls.get(i).onOpened();
    	}
		refreshControls();
    }
	
	@Override
	public void onClosed()
	{
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).onClosed();
		}
		//eventBus.removeAllEventListeners();
	}
	
	//================Events================
	
	@Override
	public void onLoseFocus()
	{
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).onLoseFocus();
		}
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled)
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			Vec3d pos = controls.get(i).rotateMouseVec(mouse);			
			if(controls.get(i).isInteractable() && controls.get(i).isMouseOver((int)pos.xCoord, (int)pos.yCoord) && controls.get(i).mouseScrolled((int)pos.xCoord, (int)pos.yCoord, scrolled))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button)
	{
		boolean result = false;
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			Vec3d pos = controls.get(i).rotateMouseVec(mouse);			
			if(!result && controls.get(i).isInteractable() && controls.get(i).isMouseOver((int)pos.xCoord, (int)pos.yCoord) && controls.get(i).mousePressed((int)pos.xCoord, (int)pos.yCoord, button))
				result = true;
			else
				controls.get(i).onLoseFocus();
		}
		return result;
	}
	
	@Override
	public void mouseMove(int x, int y, int button)
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			Vec3d pos = controls.get(i).rotateMouseVec(mouse);			
			if(controls.get(i).isInteractable() && controls.get(i).isMouseOver((int)pos.xCoord, (int)pos.yCoord))
				controls.get(i).mouseMove((int)pos.xCoord, (int)pos.yCoord, button);
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button)
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			Vec3d pos = controls.get(i).rotateMouseVec(mouse);			
			if(controls.get(i).isInteractable() && controls.get(i).isMouseOver((int)pos.xCoord, (int)pos.yCoord))
				controls.get(i).mouseReleased((int)pos.xCoord, (int)pos.yCoord, button);
		}
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		for (int i = 0; i < controls.size(); i++) {		
			if(controls.get(i).isInteractable() && controls.get(i).onKeyPressed(character, key))
				return true;
		}
		return false;
	}
	
	
	//================Tooltip================
	
	@Override
	public ArrayList<String> getTooltip()
	{
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			Vec3d pos = controls.get(i).rotateMouseVec(mouse);			
			if(controls.get(i).isInteractable() && controls.get(i).isMouseOver((int)pos.xCoord, (int)pos.yCoord))
			{
				ArrayList<String> tooltip = controls.get(i).getTooltip();
				if(tooltip.size() > 0)
					return tooltip;
			}
		}
		
		return new ArrayList<String>();
	}
	
	//================Internal Events================
	
	public boolean raiseEvent(ControlEvent event)
	{
		if(parent != null)
			return getParent().raiseEvent(event);
		return false;
	}
	
	public void addListener(Object listener)
	{
		if(parent != null)
			getParent().addListener(listener);
	}
	
	public void removeListener(Object listener)
	{
		if(parent != null)
			getParent().removeListener(listener);
	}

}
