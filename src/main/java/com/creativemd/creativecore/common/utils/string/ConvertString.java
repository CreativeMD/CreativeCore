package com.creativemd.creativecore.common.utils.string;

public class ConvertString extends StringConverter{

	public ConvertString() {
		super("S");
	}

	@Override
	public Class getClassOfObject() {
		return String.class;
	}

	@Override
	public String toString(Object object) {
		return saveString((String) object);
	}

	@Override
	public Object parseObject(String input) {
		return loadString(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
