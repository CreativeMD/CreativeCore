package com.creativemd.creativecore.common.utils.string;

import java.lang.reflect.Array;

import org.apache.commons.lang3.ClassUtils;

import scala.Char;
import scala.reflect.internal.AnnotationInfos.ClassfileAnnotArg;

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
		//Object[] array = (Object[]) object;
		Object[] allobjs = new Object[Array.getLength(object)+1];
		if(object.getClass().getComponentType().isPrimitive())
			allobjs[0] = object.getClass().getComponentType().getCanonicalName();
		else
			allobjs[0] = object.getClass().getComponentType().getName();
		for (int i = 0; i < Array.getLength(object); i++) {
			allobjs[i+1] = Array.get(object, i);
		}
		return StringUtils.ObjectsToString(allobjs);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length >= 2)
		{
			String className = (String) objects[0];
			Object parseObjects;
			try {
				Class classArray = null;
				try{
					classArray = Class.forName(className);
				}catch(Exception e){
					if(className.equals("byte")){
						classArray = Byte.class;
					}
					if(className.equals("short")){
						classArray = Short.class;
					}
					if(className.equals("int")){
						classArray = Integer.class;
					}
					if(className.equals("long")){
						classArray = Long.class;
					}
					if(className.equals("float")){
						classArray = Float.class;
					}
					if(className.equals("double")){
						classArray = Double.class;
					}
					if(className.equals("boolean")){
						classArray = Boolean.class;
					}
					if(className.equals("char")){
						classArray = Character.class;
					}
				}
				parseObjects = (Object[]) Array.newInstance(classArray, objects.length-1);
				
			} catch (Exception e) {
				e.printStackTrace();
				parseObjects = new Object[objects.length-1];
			}
			for (int i = 0; i < Array.getLength(parseObjects); i++) {
				Array.set(parseObjects, i, objects[i+1]);
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
