package com.creativemd.creativecore.common.utils.string;

public class ConvertByte extends StringConverter{

	public ConvertByte() {
		super("Byte");
	}

	@Override
	public Class getClassOfObject() {
		return Byte.class;
	}

	@Override
	public String toString(Object object) {
		return Byte.toString((Byte)object);
	}

	@Override
	public Object parseObject(String input) {
		return Byte.parseByte(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
