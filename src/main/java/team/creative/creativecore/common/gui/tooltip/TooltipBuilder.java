package team.creative.creativecore.common.gui.tooltip;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.util.text.TextFormatting;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class TooltipBuilder {
	
	private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
	private final StringBuilder builder;
	
	public TooltipBuilder() {
		builder = new StringBuilder();
	}
	
	public TooltipBuilder append(String text) {
		builder.append(text);
		return this;
	}
	
	public TooltipBuilder append(float number) {
		return append(number, false);
	}
	
	public TooltipBuilder append(float number, boolean rounded) {
		if (rounded)
			builder.append(Math.round(number));
		else
			builder.append(format.format(number));
		return this;
	}
	
	public TooltipBuilder append(double number) {
		return append(number, false);
	}
	
	public TooltipBuilder append(double number, boolean rounded) {
		if (rounded)
			builder.append(Math.round(number));
		else
			builder.append(format.format(number));
		return this;
	}
	
	public TooltipBuilder newLine() {
		builder.append("\n");
		return this;
	}
	
	public TooltipBuilder appendColor(int color) {
		int r = ColorUtils.getRed(color);
		int g = ColorUtils.getGreen(color);
		int b = ColorUtils.getBlue(color);
		int a = ColorUtils.getAlpha(color);
		builder.append("" + TextFormatting.RED + r + " " + TextFormatting.GREEN + g + " " + TextFormatting.BLUE + b + (a < 255 ? " " + TextFormatting.WHITE + a : ""));
		return this;
	}
	
	public String build() {
		return builder.toString();
	}
	
}
