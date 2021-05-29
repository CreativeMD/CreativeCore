package team.creative.creativecore.common.util.math.utils;

public class IntegerUtils {
    
    public static boolean bitIs(int number, int index) {
        return (number & (1 << (index))) > 0;
    }
    
    private static int getMask(int offset) {
        return 1 << offset;
    }
    
    public static int set(int number, int index, boolean value) {
        if (value)
            return set(number, index);
        return unset(number, index);
    }
    
    public static int unset(int number, int index) {
        return number & ~getMask(index);
    }
    
    public static int set(int number, int index) {
        return number | getMask(index);
    }
}
