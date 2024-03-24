package team.creative.creativecore.common.gui;

public class ValueParserUtil {
    public static double safeDivide(double v1, double v2) {
        if (v1 == 0 || v2 == 0)
            return 0;
        return v1 / v2;
    }
    
    public static long safePercent(long v1, long v2) {
        if (v1 == 0 || v2 == 0)
            return 0;
        return v1 % v2;
    }
    
    public static float round(double value) {
        return value != 0 ? Math.round(value * 100F) / 100F : 0;
    }
}