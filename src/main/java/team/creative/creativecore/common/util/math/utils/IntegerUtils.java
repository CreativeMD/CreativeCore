package team.creative.creativecore.common.util.math.utils;

public class IntegerUtils {
    
    public static boolean bitIs(int number, int index) {
        return (number & getMask(index)) != 0;
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
    
    public static String print(int number) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                builder.append((bitIs(number, 31 - (i * 8 + j)) ? "1" : "0"));
            }
            builder.append(" ");
        }
        return builder.toString();
    }
}
