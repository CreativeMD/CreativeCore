package com.creativemd.creativecore.common.utils.string;

public class ConvertDouble extends StringConverter{

	public ConvertDouble() {
		super("Double");
	}

	@Override
	public Class getClassOfObject() {
		return Double.class;
	}

	@Override
	public String toString(Object object) {
		return Double.toString((Double) object);
	}

	@Override
	public Object parseObject(String input) {
		return Double.parseDouble(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
