package team.creative.creativecore.common.gui.style;

public class ControlStyle {
	
	public static final ControlStyle PROGRESSBAR = new ControlStyle(ControlStyleBorder.SMALL, 0, ControlStyleFace.BAR);
	public static final ControlStyle CLICKABLE = new ControlStyle(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
	public static final ControlStyle NESTED = new ControlStyle(ControlStyleBorder.SMALL, 2, ControlStyleFace.NESTED_BACKGROUND);
	public static final ControlStyle GUI = new ControlStyle(ControlStyleBorder.BIG, 5, ControlStyleFace.BACKGROUND);
	public static final ControlStyle TRANSPARENT = new ControlStyle(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE);
	
	public final ControlStyleBorder border;
	public final int margin;
	public final ControlStyleFace face;
	
	public ControlStyle(ControlStyleBorder border, int margin, ControlStyleFace face) {
		this.border = border;
		this.margin = margin;
		this.face = face;
	}
	
	public static enum ControlStyleBorder {
		
		BIG, SMALL, NONE;
		
	}
	
	public static enum ControlStyleFace {
		
		BAR, CLICKABLE, NESTED_BACKGROUND, BACKGROUND, NONE;
		
	}
	
}
