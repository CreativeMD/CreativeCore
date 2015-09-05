package com.creativemd.creativecore.common.utils.string;

public class ConvertIntArray extends StringConverter{

	public ConvertIntArray() {
		super("IntArray");
	}

	@Override
	public Class getClassOfObject() {
		return int[].class;
	}

	@Override
	public String toString(Object object) {
		Integer[] Integers = (Integer[]) object;		
		return StringUtils.ObjectsToString((Object[])Integers);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		int[] Integers = new int[objects.length];
		for (int i = 0; i < Integers.length; i++) {
			Integers[i] = (Integer)objects[i];
		}
		return Integers;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
