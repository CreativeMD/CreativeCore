package team.creative.creativecore.common.util.mc;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.ChatFormatting;

public class TooltipUtils {
    
    public static String printColor(int color) {
        int r = ColorUtils.red(color);
        int g = ColorUtils.green(color);
        int b = ColorUtils.blue(color);
        int a = ColorUtils.alpha(color);
        return "" + ChatFormatting.RED + r + " " + ChatFormatting.GREEN + g + " " + ChatFormatting.BLUE + b + (a < 255 ? " " + ChatFormatting.WHITE + a : "");
    }
    
    private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    
    public static String printNumber(int value) {
        return format.format(value);
    }
}
