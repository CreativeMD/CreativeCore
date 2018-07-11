package com.creativemd.creativecore.common.utils.math;

public class BooleanUtils {
	
	public static boolean oneTrue(boolean... array)
	{
		boolean wasTrue = false;
		for (int i = 0; i < array.length; i++) {
			if(array[i])
				if(wasTrue)
					return false;
				else
					wasTrue = true;
		}
		return wasTrue;
	}
	
}
