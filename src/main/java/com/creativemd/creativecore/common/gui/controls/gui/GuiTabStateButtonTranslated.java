package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

public class GuiTabStateButtonTranslated extends GuiTabStateButton {
	
	public String prefix;
	
	public String[] translated;
	
	public GuiTabStateButtonTranslated(String name, int selected, String prefix, int x, int y, int height, String... states) {
		super(name, selected, x, y, height, states);
		this.prefix = prefix;
		this.translated = new String[states.length];
		for (int i = 0; i < states.length; i++)
			translated[i] = translateOrDefault(prefix + "." + states[i], states[i]);
		this.width = GuiRenderHelper.instance.getStringWidth(String.join("", translated)) + 5 * translated.length - 5 + (getContentOffset() + 2) * 2;
	}
	
	@Override
	protected String getDisplay(int index) {
		return translated[index];
	}
	
}
