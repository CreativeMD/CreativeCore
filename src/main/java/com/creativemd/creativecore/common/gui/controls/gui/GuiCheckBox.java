package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.init.SoundEvents;

public class GuiCheckBox extends GuiControl {
	
	public static final int disabledColor = ColorUtils.RGBAToInt(new Color(100, 100, 100));
	public static final int checkBoxWidth = 7;
	
	public boolean value = false;
	public String title;
	
	public GuiCheckBox(String name, String title, int x, int y, boolean value) {
		super(name, x, y, checkBoxWidth + GuiRenderHelper.instance.getStringWidth(title) + 3, 15);
		this.value = value;
		this.title = title;
	}
	
	public GuiCheckBox(String title, int x, int y, boolean value) {
		this(title, title, x, y, value);
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		int yoffset = 3;
		
		if (!enabled)
			style.getDisableEffect(this).renderStyle(0, yoffset, helper, checkBoxWidth, checkBoxWidth);
		
		style.getBorder(this).renderStyle(0, yoffset, helper, checkBoxWidth, checkBoxWidth);
		style.getBackground(this).renderStyle(1, yoffset + 1, helper, checkBoxWidth - 2, checkBoxWidth - 2);
		
		if (value)
			helper.font.drawString("x", 1, yoffset - 1, enabled ? ColorUtils.WHITE : disabledColor);
		
		helper.font.drawStringWithShadow(title, checkBoxWidth + 3, 3, enabled ? ColorUtils.WHITE : disabledColor);
	}
	
	@Override
	public boolean hasBorder() {
		return false;
	}
	
	@Override
	public boolean hasBackground() {
		return false;
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button) {
		playSound(SoundEvents.UI_BUTTON_CLICK);
		this.value = !value;
		raiseEvent(new GuiControlChangedEvent(this));
		return true;
	}
	
	@Override
	protected void renderForeground(GuiRenderHelper helper, Style style) {
		
	}
	
}
