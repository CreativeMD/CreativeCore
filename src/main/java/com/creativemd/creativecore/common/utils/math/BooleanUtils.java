package com.creativemd.creativecore.common.utils.math;

public class BooleanUtils {
	
	public static boolean oneTrue(boolean... array) {
		boolean wasTrue = false;
		for (int i = 0; i < array.length; i++) {
			if (array[i])
				if (wasTrue)
					return false;
				else
					wasTrue = true;
		}
		return wasTrue;
	}
	
	public static int countTrue(boolean... array) {
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i])
				count++;
		}
		return count;
	}
	
	public static void reset(boolean[] state) {
		for (int i = 0; i < state.length; i++)
			state[i] = false;
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
	
}
