package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;

public class GuiStateButton extends GuiButton {
	
	public String[] states;
	
	public GuiStateButton(String name, int index, int x, int y, int width, int height, String... states) {
		super(name, x, y, width, height);
		if (index >= 0 && index < states.length)
			this.caption = states[index];
		else
			this.caption = states[0];
		this.states = states;
	}
	
	public GuiStateButton(String name, int index, int x, int y, int width, String... states) {
		this(name, index, x, y, width, 14, states);
	}
	
	public GuiStateButton(String name, int index, int x, int y, String... states) {
		this(name, index, x, y, 15, states);
		this.width = GuiRenderHelper.instance.getStringWidth(caption) + getContentOffset() * 2;
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
			onClicked(posX, posY, button);
			return true;
		}
		return false;
	}
	
}
