package team.creative.creativecore.common.util.math.utils;

import java.util.Arrays;

public class BooleanUtils {
    
    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }
    
    public static boolean[] asArray(boolean value) {
        if (value)
            return new boolean[] { true };
        return new boolean[] { false };
    }
    
    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }
    
    public static boolean isFalse(Boolean value) {
        return value != null && !value;
    }
    
    public static boolean explicitOneTrue(boolean... array) {
        boolean wasTrue = false;
        for (int i = 0; i < array.length; i++)
            if (array[i])
                if (wasTrue)
                    return false;
                else
                    wasTrue = true;
        return wasTrue;
    }
    
    public static int countTrue(boolean... array) {
        int count = 0;
        for (int i = 0; i < array.length; i++)
            if (array[i])
                count++;
        return count;
    }
    
    public static void set(boolean[] state, boolean[] newState) {
        for (int i = 0; i < state.length; i++)
            if (i < newState.length)
                state[i] = newState[i];
            else
                state[i] = false;
    }
    
    public static void reset(boolean[] state) {
        Arrays.fill(state, false);
    }
    
    public static boolean any(boolean[] state) {
        for (int i = 0; i < state.length; i++)
            if (state[i])
                return true;
        return false;
    }
    
    public static void or(boolean[] state, boolean[] state2) {
        for (int i = 0; i < state.length; i++)
            state[i] = state[i] || state2[i];
    }
    
    public static boolean equals(boolean[] state, boolean[] state2) {
        for (int i = 0; i < state.length; i++)
            if (state[i] != state2[i])
                return false;
        return true;
    }
    
    public static int boolToInt(boolean[] state) {
        if (state.length > 32)
            throw new RuntimeException("Cannot convert more than 32 bits to an integer");
        int n = 0;
        for (int i = 0; i < state.length; i++)
            n = (n << 1) | (state[i] ? 1 : 0);
        return n;
    }
    
    public static void intToBool(int number, boolean[] state) {
        if (state.length > 32)
            throw new RuntimeException("More than 32 bits cannot be stored in an integer");
        for (int i = 0; i < state.length; i++)
            state[state.length - 1 - i] = (1 << i & number) != 0;
    }
    
    public static String print(boolean[] state) {
        return Arrays.toString(state);
    }
    
    public static int getRequiredBandwidth(int number) {
        int digit = 0;
        while (number != 0) {
            digit++;
            number = number / 10;
        }
        return digit;
    }
    
    public static boolean[] toBits(int number, int bandwidth) {
        boolean[] b = new boolean[bandwidth];
        for (int i = 0; i < bandwidth; i++)
            b[b.length - i - 1] = (1 << bandwidth - i - 1 & number) != 0;
        return b;
    }
    
    public static int toNumber(boolean[] b) {
        int x = 0;
        for (int i = b.length - 1; i >= 0; i--)
            x = x << 1 | (b[i] ? 1 : 0);
        return x;
    }
    
    public static boolean[] copy(boolean[] state) {
        if (state == null)
            return null;
        boolean[] newState = new boolean[state.length];
        System.arraycopy(state, 0, newState, 0, newState.length);
        return newState;
    }
    
}
