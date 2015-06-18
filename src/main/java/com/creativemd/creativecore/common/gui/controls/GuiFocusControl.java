package com.creativemd.creativecore.common.gui.controls;

public abstract class GuiFocusControl extends GuiControl{
	
	public GuiFocusControl(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public GuiFocusControl(int x, int y, int width, int height, int rotation) {
		super(x, y, width, height, rotation);
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
