package com.creativemd.creativecore.common.gui.container;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.common.gui.CoreControl;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.Rect;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.gui.GuiFocusControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;

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
		for (int i = 0; i < controls.size(); i++) {
			updateControl(controls.get(i), i);
		}
	}
	
	public void updateControl(GuiControl control, int id) {
		control.parent = this;
		control.setID(id);
	}
	
	public void addControl(GuiControl control) {
		updateControl(control, controls.size());
		controls.add(control);
	}
	
	public boolean removeControl(GuiControl control) {
		int index = controls.indexOf(control);
		if (index != -1) {
			controls.remove(index);
			for (int i = index; i < controls.size(); i++) {
				updateControl(control, i);
			}
			return true;
		}
		return false;
	}
	
	public SubGui getOrigin() {
		if (parent instanceof SubGui)
			return (SubGui) parent;
		if (parent != null)
			return ((GuiParent) parent).getOrigin();
		return null;
	}
	
	// ================Rendering================
	
	protected int lastRenderedHeight = 0;
	
	public float getScaleFactor() {
		return 1F;
	}
	
	public double getOffsetY() {
		return 0;
	}
	
	public double getOffsetX() {
		return 0;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height, Rect relativeMaximumRect) {
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		
		float scale = getScaleFactor();
		double xOffset = getOffsetX();
		double yOffset = getOffsetY();
		
		Rect newRect = relativeMaximumRect.mergeRects(getRect());
		
		lastRenderedHeight = 0;
		
		for (int i = controls.size() - 1; i >= 0; i--) {
			GuiControl control = controls.get(i);
			
			if (control.visible && control.isVisibleInsideRect((int) -xOffset, (int) -yOffset, width, height, scale)) {
				if (control.canOverlap())
					GL11.glDisable(GL11.GL_STENCIL_TEST);
				else {
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					prepareContentStencil(helper, relativeMaximumRect);
					
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
					GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
				}
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(xOffset, yOffset, 0);
				control.renderControl(helper, scale, newRect.getOffsetRect((int) xOffset, (int) yOffset));
				GlStateManager.popMatrix();
				
				if (!control.canOverlap())
					GL11.glDisable(GL11.GL_STENCIL_TEST);
			}
			
			lastRenderedHeight = (int) Math.max(lastRenderedHeight, (control.posY + control.height) * scale);
			
		}
		
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		prepareContentStencil(helper, relativeMaximumRect);
		
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0x1);
		
		renderContent(helper, style, width, height);
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
	
	// ================Helper================
	
	@Override
	public CoreControl get(String name) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.is(name))
				return control;
			if (control instanceof IControlParent) {
				CoreControl tempcontrol = ((IControlParent) control).get(name);
				if (tempcontrol != null)
					return tempcontrol;
			}
		}
		return null;
	}
	
	@Override
	public boolean has(String name) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.name.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	// ================Interaction================
	
	@Override
	public boolean isMouseOver() {
		if (parent != null)
			return super.isMouseOver();
		return true;
	}
	
	@Override
	public Vec3d getMousePos() {
		if (parent != null)
			return getParent().getMousePos().addVector(-getContentOffset() - getOffsetX() - this.posX, -getContentOffset() - getOffsetY() - this.posY, 0);
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		int i = scaledresolution.getScaledWidth();
		int j = scaledresolution.getScaledHeight();
		// int mouseX = Mouse.getX() * width / client.displayWidth;
		// int mouseZ = height - Mouse.getY() * height / client.displayHeight - 1;
		int x = Mouse.getX() * i / mc.displayWidth;
		int y = j - Mouse.getY() * j / mc.displayHeight - 1;
		int movex = (i - width) / 2;
		int movey = (j - height) / 2;
		x -= movex;
		y -= movey;
		return new Vec3d(x - getContentOffset() - getOffsetX(), y - getContentOffset() - getOffsetY(), 0);
	}
	
	public boolean isAnyControlFocused() {
		return getFocusedControl(true) != null;
	}
	
	public GuiControl getFocusedControl(boolean parent) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control instanceof GuiParent) {
				GuiControl focused = ((GuiParent) control).getFocusedControl(false);
				if (focused != null)
					return focused;
			}
			if (control instanceof GuiFocusControl && ((GuiFocusControl) control).focused)
				return control;
		}
		if (parent && getParent() != null)
			return getParent().getFocusedControl(true);
		return null;
	}
	
	// ================Custom Events================
	
	@Override
	public void onOpened() {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.parent = this;
			control.onOpened();
		}
		refreshControls();
	}
	
	@Override
	public void onClosed() {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onClosed();
		}
		// eventBus.removeAllEventListeners();
	}
	
	@Override
	public void onTick() {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onTick();
		}
	}
	
	// ================Events================
	
	@Override
	public void onLoseFocus() {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			control.onLoseFocus();
		}
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled) {
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (control.isInteractable() && control.isMouseOver((int) pos.x, (int) pos.y) && control.mouseScrolled((int) pos.x, (int) pos.y, scrolled))
				return true;
		}
		return false;
	}
	
	protected void clickControl(GuiControl control, int x, int y, int button) {
		raiseEvent(new GuiControlClickEvent(control, x, y, button));
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		boolean result = false;
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (!result && control.isInteractable() && control.isMouseOver((int) pos.x, (int) pos.y) && control.mousePressed((int) pos.x, (int) pos.y, button)) {
				clickControl(control, x, y, button);
				result = true;
			} else
				control.onLoseFocus();
		}
		return result;
	}
	
	@Override
	public void mouseMove(int x, int y, int button) {
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (control.isInteractable())
				control.mouseMove((int) pos.x, (int) pos.y, button);
		}
	}
	
	@Override
	public void mouseDragged(int x, int y, int button, long time) {
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (control.isInteractable())
				control.mouseDragged((int) pos.x, (int) pos.y, button, time);
		}
	}
	
	@Override
	public void mouseReleased(int x, int y, int button) {
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (control.isInteractable())
				control.mouseReleased((int) pos.x, (int) pos.y, button);
		}
	}
	
	@Override
	public boolean onKeyPressed(char character, int key) {
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			if (control.isInteractable() && control.onKeyPressed(character, key))
				return true;
		}
		return false;
	}
	
	// ================Tooltip================
	
	@Override
	public GuiToolTipEvent getToolTipEvent() {
		GuiToolTipEvent event = super.getToolTipEvent();
		if (event != null)
			return event;
		Vec3d mouse = getMousePos();
		for (int i = 0; i < controls.size(); i++) {
			GuiControl control = controls.get(i);
			Vec3d pos = control.rotateMouseVec(mouse);
			if (control.isInteractable() && control.isMouseOver((int) pos.x, (int) pos.y)) {
				event = control.getToolTipEvent();
				if (event != null)
					return event;
			}
		}
		return null;
	}
	
}
