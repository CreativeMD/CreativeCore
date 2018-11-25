package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.input.Mouse;

public abstract class GuiButtonHold extends GuiButton {
	
	public GuiButtonHold(String caption, int x, int y) {
		super(caption, x, y);
	}
	
	public GuiButtonHold(String caption, int x, int y, int width) {
		super(caption, caption, x, y, width);
	}
	
	public GuiButtonHold(String name, String caption, int x, int y, int width) {
		super(name, caption, x, y, width);
	}
	
	public GuiButtonHold(String name, String caption, int x, int y, int width, int height) {
		super(name, caption, x, y, width, height);
	}
	
	public GuiButtonHold(String caption, int x, int y, int width, int height) {
		super(caption, caption, x, y, width, height);
	}
	
	public static final int toWait = 500;
	
	public long wait = 0;
	
	@Override
	public boolean mousePressed(int x, int y, int button) {
		wait = System.currentTimeMillis();
		return super.mousePressed(x, y, button);
	}
	
	@Override
	public void mouseMove(int x, int y, int button) {
		if (isMouseOver(x, y) && wait > 0) {
			if (Mouse.isButtonDown(button)) {
				if (System.currentTimeMillis() - wait >= 500)
					onClicked(x, y, button);
			} else
				wait = 0;
		} else
			wait = 0;
	}
}
