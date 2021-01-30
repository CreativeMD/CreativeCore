package team.creative.creativecore.common.gui.style;

public class ControlFormatting {
	
	public static final ControlFormatting PROGRESSBAR = new ControlFormatting(ControlStyleBorder.SMALL, 0, ControlStyleFace.BAR);
	public static final ControlFormatting CLICKABLE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
	public static final ControlFormatting NESTED = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.NESTED_BACKGROUND);
	public static final ControlFormatting GUI = new ControlFormatting(ControlStyleBorder.BIG, 5, ControlStyleFace.BACKGROUND);
	public static final ControlFormatting TRANSPARENT = new ControlFormatting(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE);
	
	public final ControlStyleBorder border;
	public final int margin;
	public final ControlStyleFace face;
	
	public ControlFormatting(ControlStyleBorder border, int margin, ControlStyleFace face) {
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
