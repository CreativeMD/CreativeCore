package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

public class GuiStateButton extends GuiButton {
	
	public String[] states;
	
	public GuiStateButton(String name, String caption, int x, int y, int width, int height, String... states) {
		super(name, x, y, width, height);
		this.caption = caption;
		this.states = states;
	}
	
	public GuiStateButton(String name, int index, int x, int y, int width, int height, String... states) {
		this(name, states[index], x, y, width, height, states);
	}
	
	public GuiStateButton(String name, String caption, int x, int y, int width, String... states) {
		this(name, caption, x, y, width, 14, states);
	}
	
	public GuiStateButton(String name, int index, int x, int y, int width, String... states) {
		this(name, states[index], x, y, width, 14, states);
	}
	
	public GuiStateButton(String name, String caption, int x, int y, String... states) {
		this(name, caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), states);
	}
	
	public GuiStateButton(String name, int index, int x, int y, String... states) {
		this(name, states[index], x, y, GuiRenderHelper.instance.getStringWidth(states[index]), states);
	}
	
	public void setState(int index) {
		this.caption = states[index];
	}
	
	public int getState() {
		for (int i = 0; i < states.length; i++) {
			if (states[i].equals(caption))
				return i;
		}
		return -1;
	}
	
	public void previousState() {
		int state = getState();
		state--;
		if (state < 0)
			state = states.length - 1;
		if (state >= states.length)
			state = 0;
		setState(state);
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	public void nextState() {
		int state = getState();
		state++;
		if (state < 0)
			state = states.length - 1;
		if (state >= states.length)
			state = 0;
		setState(state);
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	@Override
	public void onClicked(int x, int y, int button) {
		
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button) {
		if (super.mousePressed(posX, posY, button)) {
			if (button == 1)
				previousState();
			else
				nextState();
			return true;
		}
		return false;
	}
	
}
