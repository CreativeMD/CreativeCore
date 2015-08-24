package com.creativemd.creativecore.common.gui.controls;

public abstract class GuiFocusControl extends GuiControl{
	
	public GuiFocusControl(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}
	
	public GuiFocusControl(String name, int x, int y, int width, int height, int rotation) {
		super(name, x, y, width, height, rotation);
	}

	public boolean focused = false;
	
	@Override
	public void onLoseFocus()
	{
		focused = false;
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button){
		focused = true;
		return true;
	}
	
}
