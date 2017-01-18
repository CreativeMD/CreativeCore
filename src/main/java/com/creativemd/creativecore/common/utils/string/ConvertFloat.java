package com.creativemd.creativecore.common.utils.string;

public class ConvertFloat extends StringConverter{

	public ConvertFloat() {
		super("Float");
	}

	@Override
	public Class getClassOfObject() {
		return Float.class;
	}

	@Override
	public String toString(Object object) {
		return Float.toString((Float) object);
	}

	@Override
	public Object parseObject(String input) {
		return Float.parseFloat(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
