package com.creativemd.creativecore.common.gui.controls;

public class GuiStateButton extends GuiButton{
	
	public String[] states;
	
	public GuiStateButton(String name, String caption, int x, int y, int width, int height, String... states) {
		super(name, x, y, width, height);
		this.caption = caption;
		this.states = states;
	}
	
	public GuiStateButton(String name, int index, int x, int y, int width, int height, String... states) {
		this(name, states[index], x, y, width, height, states);
	}
	
	public void setState(int index)
	{
		caption = states[index];
	}
	
	public int getState()
	{
		for (int i = 0; i < states.length; i++) {
			if(states[i].equals(caption))
				return i;
		}
		return -1;
	}
	
	public void nextState()
	{
		int state = getState();
		state++;
		if(state < 0)
			state = states.length-1;
		if(state >= states.length)
			state = 0;
		setState(state);
	}
	
	@Override
	public boolean mousePressed(int posX, int posY, int button)
	{
		if(super.mousePressed(posX, posY, button))
		{
			nextState();
			return true;
		}
		return false;			
	}
	

}
