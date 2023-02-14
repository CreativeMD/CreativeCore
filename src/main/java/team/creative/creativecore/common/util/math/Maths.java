package team.creative.creativecore.common.util.math;

public class Maths {
    
    public static final float EPSILON = 0.001F;
    
    public static boolean smallerThanAndEquals(double a, double b) {
        return a < b || equals(a, b);
    }
    
    public static boolean greaterThanAndEquals(double a, double b) {
        return a > b || equals(a, b);
    }
    
    public static boolean equals(double a, double b) {
        return a == b ? true : Math.abs(a - b) < EPSILON;
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
