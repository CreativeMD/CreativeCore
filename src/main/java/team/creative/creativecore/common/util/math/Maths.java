package team.creative.creativecore.common.util.math;

import net.minecraft.util.Mth;

public class Maths {
    
    public static final float EPSILON = 0.001F;
    public static final float EPSILON_UP = 1 / 0.001F;
    public static final double EPSILON_DOUBLE = EPSILON;
    public static final double EPSILON_UP_DOUBLE = 1 / EPSILON_DOUBLE;
    
    public static boolean smallerThanAndEquals(double a, double b) {
        return a < b || equals(a, b);
    }
    
    public static boolean greaterThanAndEquals(double a, double b) {
        return a > b || equals(a, b);
    }
    
    public static boolean equals(double a, double b) {
        return a == b || Math.abs(a - b) < EPSILON;
    }
    
    /** 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks
     *            Minecraft Tick count
     * @return ticks converted to MS */
    public static long tickToMs(int ticks) {
        return ticks * 50L;
    }
    
    /** 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms
     *            Time in milliseconds
     * @return Milliseconds converted to Ticks */
    public static int msToTick(long ms) {
        return (int) (ms / 50);
    }
    
    public static double round(double value) {
        return Mth.floor(EPSILON_UP_DOUBLE * value + 0.5) * EPSILON_DOUBLE;
    }
    
    public static float round(float value) {
        return Mth.floor(EPSILON_UP * value + 0.5F) * EPSILON;
    }
    
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
    
    public static float safeRound(double value) {
        return value != 0 ? Math.round(value * 100F) / 100F : 0;
    }
    
    public static int min(int a, int b, int c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }
    
    public static float min(float a, float b, float c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }
    
    public static double min(double a, double b, double c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }
    
    public static long min(long a, long b, long c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }
    
    public static int max(int a, int b, int c) {
        return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
    }
    
    public static float max(float a, float b, float c) {
        return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
    }
    
    public static double max(double a, double b, double c) {
        return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
    }
    
    public static long max(long a, long b, long c) {
        return a >= b ? (a >= c ? a : c) : (b >= c ? b : c);
    }
}
