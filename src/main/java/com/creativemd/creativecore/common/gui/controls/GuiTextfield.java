package com.creativemd.creativecore.common.gui.controls;

import net.minecraft.client.gui.FontRenderer;

public class GuiTextfield extends GuiFocusControl{

	public String text;
	/**also selStart**/;
	public int cursorPosition;
	public int selEnd = -1;
	
	public GuiTextfield(String text, int x, int y, int width, int height) {
		this(text, x, y, width, height, 0);
	}
	
	public GuiTextfield(String text, int x, int y, int width, int height, int rotation) {
		super(x, y, width, height);
		this.text = text;
		
	}
	
	public boolean hasSelectedText()
	{
		return selEnd != -1 && selEnd != cursorPosition;
	}
	
	public String getSelectedText()
	{
		if(hasSelectedText())
			return text.substring(cursorPosition, selEnd);
		else
			return "";
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		super.mousePressed(posX, posY, button);
		
		return true;
	}
	
	//pub
	
	//public void onKeyPressed(int key){}
	
	@Override
	public void onKeyDown(int key)
	{
		
	}
	//public void onKeyUp(int key){}
	
	public void onTextChange(){}

	@Override
	public void drawControl(FontRenderer renderer) {
		// TODO Auto-generated method stub
		
	}

}
