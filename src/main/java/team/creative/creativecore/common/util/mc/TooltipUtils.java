package team.creative.creativecore.common.util.mc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.ChatFormatting;

public class TooltipUtils {
    
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    
    public static String printColor(int color) {
        int r = ColorUtils.red(color);
        int g = ColorUtils.green(color);
        int b = ColorUtils.blue(color);
        int a = ColorUtils.alpha(color);
        return "" + ChatFormatting.RED + r + " " + ChatFormatting.GREEN + g + " " + ChatFormatting.BLUE + b + (a < 255 ? " " + ChatFormatting.WHITE + a : "");
    }
    
    public static String print(int value) {
        return NUMBER_FORMAT.format(value);
    }
    
    public static String print(double value) {
        return DECIMAL_FORMAT.format(value);
    }
    
    public static String print(float value) {
        return DECIMAL_FORMAT.format(value);
    }
}
