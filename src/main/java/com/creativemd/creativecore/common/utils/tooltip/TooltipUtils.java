package com.creativemd.creativecore.common.utils.tooltip;

import java.text.NumberFormat;
import java.util.Locale;

import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public class TooltipUtils {
    
    public static String printRGB(int color) {
        int r = ColorUtils.getRed(color);
        int g = ColorUtils.getGreen(color);
        int b = ColorUtils.getBlue(color);
        int a = ColorUtils.getAlpha(color);
        return "" + ChatFormatting.RED + r + " " + ChatFormatting.GREEN + g + " " + ChatFormatting.BLUE + b + (a < 255 ? " " + ChatFormatting.WHITE + a : "");
    }
    
    private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    
    public static String printNumber(int value) {
        return format.format(value);
    }
}
