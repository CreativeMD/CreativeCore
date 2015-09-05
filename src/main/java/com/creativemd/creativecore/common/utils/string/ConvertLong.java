package com.creativemd.creativecore.common.utils.string;

public class ConvertLong extends StringConverter{

	public ConvertLong() {
		super("Long");
	}

	@Override
	public Class getClassOfObject() {
		return Long.class;
	}

	@Override
	public String toString(Object object) {
		return Long.toString((Long) object);
	}

	@Override
	public Object parseObject(String input) {
		return Long.parseLong(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
