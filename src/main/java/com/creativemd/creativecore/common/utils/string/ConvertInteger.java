package com.creativemd.creativecore.common.utils.string;

public class ConvertInteger extends StringConverter{

	public ConvertInteger() {
		super("I");
	}

	@Override
	public Class getClassOfObject() {
		return Integer.class;
	}

	@Override
	public String toString(Object object) {
		return Integer.toString((Integer) object);
	}

	@Override
	public Object parseObject(String input) {
		return Integer.parseInt(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
