package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;

import net.minecraft.init.SoundEvents;

public class GuiScrollBox extends GuiParent {
	
	public int maxScroll = 0;
	public int aimedScrolled = 0;
	public double scrolled = 0;
	public double beforeScrolled = 0;
	public static final long scrollTime = 200;
	public long scrollMS = 0;
	public float scaleFactor;
	public boolean dragged;
	public int scrollbarWidth = 6;
	
	public GuiScrollBox(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, 1F);
	}
	
	public GuiScrollBox(String name, int x, int y, int width, int height, float scaleFactor) {
		super(name, x, y, width, height);
		this.scaleFactor = scaleFactor;
		this.marginWidth = 0;
	}
	
	@Override
	public float getScaleFactor() {
		return scaleFactor;
	}
	
	@Override
	protected double getOffsetY() {
		return -scrolled;
	}
	
	public void onScrolled() {
		if (this.aimedScrolled < 0)
			this.aimedScrolled = 0;
		if (this.aimedScrolled > maxScroll)
			this.aimedScrolled = maxScroll;
	}
	
	@Override
	public boolean mouseScrolled(int x, int y, int scrolled) {
		if (super.mouseScrolled(x, y, scrolled))
			return true;
		scrollMS = System.currentTimeMillis();
		this.aimedScrolled -= scrolled * 20;
		onScrolled();
		beforeScrolled = this.scrolled;
		return true;
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		if (button == 0 && width - x + this.posX <= scrollbarWidth && needsScrollbar()) {
			playSound(SoundEvents.UI_BUTTON_CLICK);
			dragged = true;
			return true;
		}
		return super.mousePressed(x, y, button);
	}
	
	@Override
	public void mouseMove(int x, int y, int button) {
		if (dragged) {
			double percent = (double) (y - this.posY) / (double) (height);
			aimedScrolled = (int) (percent * maxScroll);
			scrollMS = System.currentTimeMillis();
			
			onScrolled();
			beforeScrolled = scrolled;
			/*
			 * scrolled = aimedScrolled; beforeScrolled = scrolled;
			 */
		}
		super.mouseMove(x, y, button);
	}
	
	@Override
	public void mouseReleased(int x, int y, int button) {
		super.mouseReleased(x, y, button);
		dragged = false;
	}
	
	public boolean needsScrollbar() {
		return lastRenderedHeight > this.height - getContentOffset() * 2;
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		super.renderContent(helper, style, width - scrollbarWidth, height);
		style.getBorder(this).renderStyle(width - scrollbarWidth, 0, helper, scrollbarWidth, height);
		style.getMouseOverBackground(this).renderStyle(width - scrollbarWidth + 1, 0, helper, scrollbarWidth - 1, height);
		
		if (scrollMS != 0) {
			if (scrollMS + scrollTime <= System.currentTimeMillis()) {
				scrolled = aimedScrolled;
				beforeScrolled = scrolled;
				scrollMS = 0;
			} else
				scrolled = beforeScrolled + (aimedScrolled - beforeScrolled) * ((System.currentTimeMillis() - scrollMS) / (double) scrollTime);
		}
		
		int scrollThingHeight = Math.max(10, Math.min(height, lastRenderedHeight / height / height));
		if (lastRenderedHeight < height)
			scrollThingHeight = height;
		double percent = (double) scrolled / (double) maxScroll;
		// style.getBorder(this).renderStyle(width-scrollbarWidth+1, (int)
		// (percent*(height-scrollThingHeight)), helper, scrollbarWidth-1,
		// scrollThingHeight);
		style.getFace(this).renderStyle(width - scrollbarWidth + 1, (int) (percent * (height - scrollThingHeight)), helper, scrollbarWidth - 1, scrollThingHeight);
		
		maxScroll = Math.max(0, (lastRenderedHeight - height)) + 5;
	}
	
	@Override
	public boolean hasMouseOverEffect() {
		return false;
	}
}
