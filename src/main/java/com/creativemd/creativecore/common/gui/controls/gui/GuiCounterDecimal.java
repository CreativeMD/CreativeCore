package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;

import net.minecraft.util.math.MathHelper;

public class GuiCounterDecimal extends GuiParent {
	
	public float min;
	public float max;
	public GuiTextfield textfield;
	
	public GuiCounterDecimal(String name, int x, int y, int width, float value, float min, float max) {
		super(name, x, y, width, 10);
		this.min = min;
		this.max = max;
		this.marginWidth = 0;
		setStyle(Style.emptyStyle);
		textfield = new GuiTextfield("value", "" + MathHelper.clamp(value, min, max), 0, 0, width - 11, 8).setNumbersOnly();
		addControl(textfield.setStyle(defaultStyle));
		addControl(new GuiButton("-", "-", width - 6, 7, 4, 1) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				textfield.text = "" + Float.toString(stepDown(textfield.parseFloat()));
			}
		}.setStyle(defaultStyle));
		addControl(new GuiButton("+", "+", width - 6, 0, 4, 2) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				textfield.text = "" + Float.toString(stepUp(textfield.parseFloat()));
			}
		}.setStyle(defaultStyle));
		
	}
	
	public float stepUp(float value) {
		return Math.min(max, value + 1);
	}
	
	public float stepDown(float value) {
		return Math.max(min, value - 1);
	}
	
	public float getValue() {
		return MathHelper.clamp(textfield.parseFloat(), min, max);
	}
	
	public void setValue(float value) {
		textfield.text = "" + Float.toString(MathHelper.clamp(value, min, max));
	}
}
