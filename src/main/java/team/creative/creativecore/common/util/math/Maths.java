package team.creative.creativecore.common.util.math;

public class Maths {
    
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
