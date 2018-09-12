package com.creativemd.creativecore.gui.client.style;

import com.creativemd.creativecore.gui.GuiControl;

public class Style {

	public static Style liteStyle = new Style("default", new ColoredDisplayStyle(0, 0, 0), new ColoredDisplayStyle(90, 90, 90), new ColoredDisplayStyle(100, 100, 100), new ColoredDisplayStyle(198, 198, 198), new ColoredDisplayStyle(0, 0, 0, 100));

	public static Style liteStyleNoHighlight = new Style("defaultNoHigh", new ColoredDisplayStyle(0, 0, 0), new ColoredDisplayStyle(90, 90, 90), new ColoredDisplayStyle(90, 90, 90), new ColoredDisplayStyle(198, 198, 198), new ColoredDisplayStyle(0, 0, 0, 100));

	public static Style emptyStyle = new Style("empty", DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay);

	public final String name;

	private DisplayStyle border;
	private DisplayStyle mouseOverBackground;
	private DisplayStyle background;
	private DisplayStyle face;
	private DisplayStyle disableEffect;

	public Style(String name, DisplayStyle border, DisplayStyle background, DisplayStyle mouseOverBackground, DisplayStyle face, DisplayStyle disableEffect) {
		this.name = name;
		this.border = border;
		this.background = background;
		this.mouseOverBackground = mouseOverBackground;
		this.face = face;
		this.disableEffect = disableEffect;
	}

	public DisplayStyle getBorder(GuiControl control) {
		return border;
	}

	public DisplayStyle getMouseOverBackground(GuiControl control) {
		return mouseOverBackground;
	}

	public DisplayStyle getBackground(GuiControl control) {
		if (control != null && control.hasMouseOverEffect() && control.isMouseOver())
			return mouseOverBackground;
		return background;
	}

	public DisplayStyle getFace(GuiControl control) {
		return face;
	}

	public DisplayStyle getDisableEffect(GuiControl control) {
		return disableEffect;
	}
}
