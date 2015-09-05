package com.creativemd.creativecore.common.utils.string;

import java.lang.reflect.Array;

public class ConvertArray extends StringConverter{

	public ConvertArray() {
		super("A");
	}

	@Override
	public Class getClassOfObject() {
		return Object[].class;
	}

	@Override
	public String toString(Object object) {
		Object[] array = (Object[]) object;
		Object[] allobjs = new Object[array.length+1];
		allobjs[0] = array.getClass().getComponentType().getName();
		for (int i = 0; i < array.length; i++) {
			allobjs[i+1] = array[i];
		}
		return StringUtils.ObjectsToString(allobjs);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length >= 2)
		{
			String className = (String) objects[0];
			Object[] parseObjects;
			try {
				parseObjects = (Object[]) Array.newInstance(Class.forName(className), objects.length-1);
			} catch (Exception e) {
				parseObjects = new Object[objects.length-1];
			}
			for (int i = 0; i < parseObjects.length; i++) {
				parseObjects[i] = objects[i+1];
			}
			return parseObjects;
		}
		return new Object[0];
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}
	
	@Override
	public boolean isConverter(Class ObjectClass)
	{
		return getClassOfObject().isArray();
	}

}
