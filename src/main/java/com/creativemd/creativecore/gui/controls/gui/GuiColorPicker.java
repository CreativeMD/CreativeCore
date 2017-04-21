package com.creativemd.creativecore.gui.controls.gui;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.ColorUtils.ColorPart;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.GuiParent;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

public class GuiColorPicker extends GuiParent {
	
	public Color color;
	
	public GuiColorPicker(String name, int x, int y, Color color) {
		super(name, x, y, 140, 30);
		marginWidth = 0;
		this.color = color;
		setStyle(Style.emptyStyle);
		
		addControl(new GuiColoredSteppedSlider("r", 0, 0, 100, 5, this, ColorPart.RED).setStyle(defaultStyle));
		addControl(new GuiColoredSteppedSlider("g", 0, 10, 100, 5, this, ColorPart.GREEN).setStyle(defaultStyle));
		addControl(new GuiColoredSteppedSlider("b", 0, 20, 100, 5, this, ColorPart.BLUE).setStyle(defaultStyle));
		addControl(new GuiColorPlate("plate", 107, 2, 20, 20, color).setStyle(defaultStyle));
		
	}
	
	public void onColorChanged()
	{
		raiseEvent(new GuiControlChangedEvent(this));
	}

}
