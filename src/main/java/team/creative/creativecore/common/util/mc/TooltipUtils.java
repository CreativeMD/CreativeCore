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
    
    public static String printRoman(int input) {
        if (input < 1 || input > 3999)
            return "" + input;
        String s = "";
        while (input >= 1000) {
            s += "M";
            input -= 1000;
        }
        while (input >= 900) {
            s += "CM";
            input -= 900;
        }
        while (input >= 500) {
            s += "D";
            input -= 500;
        }
        while (input >= 400) {
            s += "CD";
            input -= 400;
        }
        while (input >= 100) {
            s += "C";
            input -= 100;
        }
        while (input >= 90) {
            s += "XC";
            input -= 90;
        }
        while (input >= 50) {
            s += "L";
            input -= 50;
        }
        while (input >= 40) {
            s += "XL";
            input -= 40;
        }
        while (input >= 10) {
            s += "X";
            input -= 10;
        }
        while (input >= 9) {
            s += "IX";
            input -= 9;
        }
        while (input >= 5) {
            s += "V";
            input -= 5;
        }
        while (input >= 4) {
            s += "IV";
            input -= 4;
        }
        while (input >= 1) {
            s += "I";
            input -= 1;
        }
        return s;
    }
}
