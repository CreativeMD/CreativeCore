package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.event.ControlEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.util.math.MathHelper;

public class GuiCounter extends GuiParent {
	
	public int min;
	public int max;
	public GuiTextfield textfield;
	
	public GuiCounter(String name, int x, int y, int width, int value, int min, int max) {
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
				textfield.text = "" + stepDown(textfield.parseInteger());
				raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
			}
		}.setStyle(defaultStyle));
		addControl(new GuiButton("+", "+", width - 6, 0, 4, 2) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				textfield.text = "" + stepUp(textfield.parseInteger());
				raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
			}
		}.setStyle(defaultStyle));
		
	}
	
	@Override
	public boolean raiseEvent(ControlEvent event) {
		if (event instanceof GuiControlChangedEvent && event.source.is("value"))
			return super.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
		else
			return super.raiseEvent(event);
	}
	
	public int stepUp(int value) {
		return Math.min(max, value + 1);
	}
	
	public int stepDown(int value) {
		return Math.max(min, value - 1);
	}
	
	public int getValue() {
		return MathHelper.clamp(textfield.parseInteger(), min, max);
	}
	
	public void setValue(int value) {
		textfield.text = "" + MathHelper.clamp(value, min, max);
	}
	
}
