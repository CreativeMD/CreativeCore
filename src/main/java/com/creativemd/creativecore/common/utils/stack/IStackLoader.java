package com.creativemd.creativecore.common.utils.stack;

public interface IStackLoader {
	
	public StackInfo getStackInfo(Object item);
	
	public StackInfo getStackInfoFromString(String input);
}
