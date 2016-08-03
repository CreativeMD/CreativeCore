package com.creativemd.creativecore.gui.controls.gui;

public class GuiIDButton extends GuiButton {
	
	public final int id;
	
	public GuiIDButton(String caption, int x, int y, int id) {
		super(caption, x, y);
		this.id = id;
	}

	@Override
	public void onClicked(int x, int y, int button) {}

}
