package com.creativemd.creativecore.gui.core.container;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.core.CoreControl;
import com.creativemd.creativecore.gui.core.GuiControl;
import com.creativemd.creativecore.gui.core.GuiRenderHelper;

public class GuiParent extends GuiControl implements IControlParent {
	
	public ArrayList<GuiControl> controls = new ArrayList<>();
	
	public GuiParent(String name, int x, int y, int width, int height) {
		super(name, x, y, width, height);
	}

	@Override
	public List getControls() {
		return controls;
	}

	@Override
	public void refreshControls() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
		// TODO Auto-generated method stub
		
	}

}
