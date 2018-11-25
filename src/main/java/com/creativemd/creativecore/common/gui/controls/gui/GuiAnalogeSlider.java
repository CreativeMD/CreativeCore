package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.input.Keyboard;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.init.SoundEvents;

public class GuiAnalogeSlider extends GuiControl {
	public float maxValue;
	public float minValue;
	public float value;
	public boolean grabbedSlider;
	public int sliderWidth = 4;
	
	protected GuiTextfield textfield = null;
	
	public GuiAnalogeSlider(String name, int x, int y, int width, int height, float value, float minValue, float maxValue) {
		super(name, x, y, width, height);
		this.marginWidth = 0;
		this.minValue = minValue;
		this.maxValue = maxValue;
		setValue(value);
	}
	
	public String getTextByValue() {
		return Math.round(value * 100F) / 100F + "";
	}
	
	public String getTextfieldValue() {
		return getTextByValue();
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		
		float percent = getPercentage();
		
		int posX = (int) ((this.width - (borderWidth * 2 + sliderWidth)) * percent);
		style.getFace(this).renderStyle(posX, 0, helper, 4, height);
		
		if (textfield != null)
			textfield.renderControl(helper, 1, getRect());
		else
			helper.drawStringWithShadow(getTextByValue(), width, height, ColorUtils.WHITE);
	}
	
	public float getPercentage() {
		return (this.value - this.minValue) / (this.maxValue - this.minValue);
	}
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		if (button == 0) {
			if (textfield != null)
				return textfield.mousePressed(x, y, button);
			playSound(SoundEvents.UI_BUTTON_CLICK);
			return (grabbedSlider = true);
		} else if (button == 1) {
			grabbedSlider = false;
			textfield = createTextfield();
			textfield.focused = true;
			textfield.setCursorPosition(textfield.text.length());
			textfield.parent = parent;
			return true;
		}
		return false;
	}
	
	protected GuiTextfield createTextfield() {
		return new GuiTextfield(getTextfieldValue(), 0, 0, width - getContentOffset() * 8, height - getContentOffset() * 8).setFloatOnly();
	}
	
	public void closeTextField() {
		float value = this.value;
		try {
			setValue(Float.parseFloat(textfield.text));
			playSound(SoundEvents.UI_BUTTON_CLICK);
		} catch (NumberFormatException e) {
			setValue(value);
		}
		textfield = null;
	}
	
	@Override
	public boolean onKeyPressed(char character, int key) {
		if (textfield != null) {
			if (key == Keyboard.KEY_RETURN) {
				closeTextField();
				return true;
			}
			return textfield.onKeyPressed(character, key);
		}
		return super.onKeyPressed(character, key);
	}
	
	public void setValue(float value) {
		this.value = Math.max(minValue, value);
		this.value = Math.min(maxValue, this.value);
		
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button) {
		if (grabbedSlider) {
			int width = this.width - getContentOffset() * 2 - sliderWidth;
			
			if (posX < this.posX + getContentOffset())
				this.value = this.minValue;
			else if (posX > this.posX + getContentOffset() + width + sliderWidth / 2)
				this.value = this.maxValue;
			else {
				int mouseOffsetX = posX - this.posX - getContentOffset() - sliderWidth / 2;
				this.value = (float) (this.minValue + (float) ((this.maxValue - this.minValue) * ((float) mouseOffsetX / (float) width)));
			}
			setValue(value);
		}
	}
	
	@Override
	public void onLoseFocus() {
		if (textfield != null)
			closeTextField();
		super.onLoseFocus();
	}
	
	@Override
	public void mouseReleased(int posX, int posY, int button) {
		if (this.grabbedSlider)
			grabbedSlider = false;
	}
}
