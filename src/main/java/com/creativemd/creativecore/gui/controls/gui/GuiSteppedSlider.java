package com.creativemd.creativecore.gui.controls.gui;

public class GuiSteppedSlider extends GuiAnalogeSlider{
	
	public GuiSteppedSlider(String name, int x, int y, int width, int height, int min, int max, int value)
	{
		super(name, x, y, width, height, min, max, value);
	}
	
	@Override
	public String getTextByValue()
	{
		return ((int)value) + "";
	}
	
	@Override
	public void mouseMove(int posX, int posY, int button){
		super.mouseMove(posX, posY, button);
		value = (int) value;
	}
	
	public void setValue(float value)
	{
		super.setValue((int) value); 
	}
}
