package com.creativemd.creativecore.common.utils.string;

public class ConvertByteArray extends StringConverter{

	public ConvertByteArray() {
		super("ByteArray");
	}

	@Override
	public Class getClassOfObject() {
		return byte[].class;
	}

	@Override
	public String toString(Object object) {
		Byte[] bytes = (Byte[]) object;		
		return StringUtils.ObjectsToString((Object)bytes);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		byte[] bytes = new byte[objects.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (Byte)objects[i];
		}
		return bytes;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
