package com.creativemd.creativecore.common.utils.math;

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
        /*switch (index) {
        case 0:
        	return number | Integer.MIN_VALUE;
        case 1:
        	return number | 0b01000000_00000000_00000000_00000000;
        case 2:
        	return number | 0b00100000_00000000_00000000_00000000;
        case 3:
        	return number | 0b00010000_00000000_00000000_00000000;
        case 4:
        	return number | 0b00001000_00000000_00000000_00000000;
        case 5:
        	return number | 0b00000100_00000000_00000000_00000000;
        case 6:
        	return number | 0b00000010_00000000_00000000_00000000;
        case 7:
        	return number | 0b00000001_00000000_00000000_00000000;
        case 8:
        	return number | 0b00000000_10000000_00000000_00000000;
        case 9:
        	return number | 0b00000000_01000000_00000000_00000000;
        case 10:
        	return number | 0b00000000_00100000_00000000_00000000;
        case 11:
        	return number | 0b00000000_00010000_00000000_00000000;
        case 12:
        	return number | 0b00000000_00001000_00000000_00000000;
        case 13:
        	return number | 0b00000000_00000100_00000000_00000000;
        case 14:
        	return number | 0b00000000_00000010_00000000_00000000;
        case 15:
        	return number | 0b00000000_00000001_00000000_00000000;
        case 16:
        	return number | 0b00000000_00000000_10000000_00000000;
        case 17:
        	return number | 0b00000000_00000000_01000000_00000000;
        case 18:
        	return number | 0b00000000_00000000_00100000_00000000;
        case 19:
        	return number | 0b00000000_00000000_00010000_00000000;
        case 20:
        	return number | 0b00000000_00000000_00001000_00000000;
        case 21:
        	return number | 0b00000000_00000000_00000100_00000000;
        case 22:
        	return number | 0b00000000_00000000_00000010_00000000;
        case 23:
        	return number | 0b00000000_00000000_00000001_00000000;
        case 24:
        	return number | 0b00000000_00000000_00000000_10000000;
        case 25:
        	return number | 0b00000000_00000000_00000000_01000000;
        case 26:
        	return number | 0b00000000_00000000_00000000_00100000;
        case 27:
        	return number | 0b00000000_00000000_00000000_00010000;
        case 28:
        	return number | 0b00000000_00000000_00000000_00001000;
        case 29:
        	return number | 0b00000000_00000000_00000000_00000100;
        case 30:
        	return number | 0b00000000_00000000_00000000_00000010;
        case 31:
        	return number | 0b00000000_00000000_00000000_00000001;
        }
        return number;*/
    }
}
