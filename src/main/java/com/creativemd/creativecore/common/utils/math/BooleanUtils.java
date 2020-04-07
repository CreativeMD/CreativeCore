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
	
}
