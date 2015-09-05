package com.creativemd.creativecore.common.utils.string;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class StringUtils {
	
	static
	{
		StringConverter.splitters.add("{");
		StringConverter.splitters.add("}");
		StringConverter.splitters.add(":");
		StringConverter.splitters.add(";");
		
		StringConverter.registerConverter(new ConvertBlock());
		StringConverter.registerConverter(new ConvertByte());
		StringConverter.registerConverter(new ConvertByteArray());
		StringConverter.registerConverter(new ConvertDouble());
		StringConverter.registerConverter(new ConvertFloat());
		StringConverter.registerConverter(new ConvertInfo());
		StringConverter.registerConverter(new ConvertIntArray());
		StringConverter.registerConverter(new ConvertInteger());
		StringConverter.registerConverter(new ConvertItem());
		StringConverter.registerConverter(new ConvertItemStack());
		StringConverter.registerConverter(new ConvertLong());
		StringConverter.registerConverter(new ConvertNBTTagCompound());
		StringConverter.registerConverter(new ConvertNBTTagList());
		StringConverter.registerConverter(new ConvertShort());
		StringConverter.registerConverter(new ConvertString());
		StringConverter.registerConverter(new ConvertArray());
	}
	
	public static String ObjectsToString(Object... objects)
	{
		String result = "{";
		for (int i = 0; i < objects.length; i++) {
			if(objects[i] != null)
			{
				StringConverter converter = StringConverter.getConverter(objects[i].getClass());
				if(converter != null)
				{
					result += converter.UUID + ":" + converter.toString(objects[i]) + ";";
				}
			}else{
				result += "null:null;";
			}
		}
		result += "}";
		return result;
	}
	
	public static Object[] StringToObjects(String input)
	{
		if(input.startsWith("{") && input.endsWith("}")) //String example: {I:1;S:jfkjfwiuej3keksa;NBT:{S:SJDkjskajd}}
		{
			int index = 0;
			int end = getBraceLength(index, input);
			ArrayList<Integer> breaks = new ArrayList<Integer>(); 
			index++;
			while(index < end)
			{
				String currentChar = input.substring(index, index+1);
				if(currentChar.equals("{"))
					index += getBraceLength(index, input)-1;
				if(currentChar.equals(";"))
					breaks.add(index);
				index++;
			}
			ArrayList objects = new ArrayList();
			for (int i = 0; i < breaks.size(); i++) {
				int beforeIndex = 1;
				if(i > 0)
					beforeIndex = breaks.get(i-1)+1;
				String objectString = StringConverter.loadString(input.substring(beforeIndex, breaks.get(i)));
				String UUID = objectString.substring(0, objectString.indexOf(":"));
				String objectInput = objectString.substring(objectString.indexOf(":")+1);
				StringConverter converter = StringConverter.getConverter(UUID);
				if(converter != null)
				{
					Object object = converter.parseObject(objectInput);
					if(object != null)
						objects.add(object);
				}else if(UUID.equals("null")){
					objects.add(null);
				}
			}
			return objects.toArray();
		}else{
			System.out.println("[CraftingManagerAPI] Invalid string: '" + input + "'");
			return new Object[0];
		}
	}
	
	protected static int getBraceLength(int StartIndex, String input)
	{
		int braces = 0;
		int index = StartIndex;
		if(input.substring(StartIndex, StartIndex+1).equals("{"))
			braces++;
		index++;
		while(index < input.length() && braces > 0)
		{
			if(input.substring(index, index+1).equals("{"))
				braces++;
			if(input.substring(index, index+1).equals("}"))
				braces--;
			index++;
		}
		return index - StartIndex;
	}
	
	protected static String getBraceString(int StartIndex, String input)
	{
		return input.substring(StartIndex, StartIndex+getBraceLength(StartIndex, input));
	}
	
}
