package com.creativemd.creativecore.common.utils.string;

public class ConvertShort extends StringConverter{

	public ConvertShort() {
		super("Short");
	}

	@Override
	public Class getClassOfObject() {
		return Short.class;
	}

	@Override
	public String toString(Object object) {
		return Short.toString((Short) object);
	}

	@Override
	public Object parseObject(String input) {
		return Short.parseShort(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
