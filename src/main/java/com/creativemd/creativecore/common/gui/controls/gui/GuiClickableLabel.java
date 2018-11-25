package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

import net.minecraft.init.SoundEvents;

public abstract class GuiClickableLabel extends GuiLabel {
	
	public GuiClickableLabel(String caption, int x, int y) {
		super(caption, x, y, 14737632);
	}
	
	public GuiClickableLabel(String caption, int x, int y, int color) {
		this(caption, caption, x, y, color);
	}
	
	public GuiClickableLabel(String name, String caption, int x, int y, int color) {
		this(name, caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), GuiRenderHelper.instance.getFontHeight(), color);
	}
	
	public GuiClickableLabel(String caption, int x, int y, int width, int height, int color) {
		this(caption, caption, x, y, width, height, color);
	}
	
	public GuiClickableLabel(String name, String caption, int x, int y, int width, int height, int color) {
		super(name, caption, x, y, width, height, color);
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		playSound(SoundEvents.UI_BUTTON_CLICK);
		onClicked(x, y, button);
		return true;
	}
	
	public abstract void onClicked(int x, int y, int button);
}
