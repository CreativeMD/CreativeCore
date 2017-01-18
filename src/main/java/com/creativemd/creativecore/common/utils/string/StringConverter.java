package com.creativemd.creativecore.common.utils.string;

import java.util.ArrayList;

public abstract class StringConverter {
	
	public static ArrayList<StringConverter> converters = new ArrayList<StringConverter>();
	
	public static ArrayList<String> splitters = new ArrayList<String>();
	
	public static final String splitcode = "/s";
	
	public static String getSplitterCode(int index, String splitter)
	{
		String Number = "" + index;
		if(index < 10)
			Number = "0" + Number;
		return splitter + Number;
	}
	
	public static String getSplitterCode(int index)
	{
		return getSplitterCode(index, splitcode);
	}
	
	/**Replaces codes with splitters**/
	public static String loadString(String input)
	{
		String splitter = splitcode;
		if(input.contains("&Split"))
			splitter = "&Split";
		String result = input.substring(0);
		for (int i = 0; i < splitters.size(); i++) {
			 result = result.replace(getSplitterCode(i, splitter), splitters.get(i));
		}
		return result;
	}
	
	/**Replaces splitters with codes**/
	public static String saveString(String input)
	{
		String result = input.substring(0);
		for (int i = 0; i < splitters.size(); i++) {
			 result = result.replace(splitters.get(i), getSplitterCode(i));
		}
		return result;
	}
	
	public static StringConverter getConverter(Class ObjectClass)
	{
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).isConverter(ObjectClass)) {
				return converters.get(i);
			}
		}
		return null;
	}
	
	public static StringConverter getConverter(String UUID)
	{
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).UUID.equals(UUID)) {
				return converters.get(i);
			}
		}
		return null;
	}
	
	public static boolean containsConverter(String UUID)
	{
		return getConverter(UUID) != null;
	}
	
	public static boolean registerConverter(StringConverter converter)
	{
		if (containsConverter(converter.UUID))
		{
			System.out.println("The UUID: '" + converter.UUID + "' is already taken!");
			return false;
		}else{
			converters.add(converter);
			String[] split = converter.getSplitter();
			for (int i = 0; i < split.length; i++) {
				if(!splitters.contains(split[i]))
					splitters.add(split[i]);
			}
			return true;
		}
	}
	
	public boolean isConverter(Class ObjectClass)
	{
		return getClassOfObject().isAssignableFrom(ObjectClass);
	}
	
	public final String UUID;
	
	public StringConverter(String UUID)
	{
		this.UUID = UUID;
	}
	
	public abstract Class getClassOfObject();
	
	public abstract String toString(Object object);
	
	public abstract Object parseObject(String input);
	
	public abstract String[] getSplitter();
}
