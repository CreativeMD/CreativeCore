package com.creativemd.creativecore.common.utils.string;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;

public class ConvertNBTTagCompound extends StringConverter{

	public ConvertNBTTagCompound() {
		super("NBTTagCompound");
	}

	@Override
	public Class getClassOfObject() {
		return NBTTagCompound.class;
	}
	
	public static Object getObject(NBTBase base)
	{
		if(base instanceof NBTTagByte)
			return ((NBTTagByte)base).func_150290_f();
		if(base instanceof NBTTagShort)
			return ((NBTTagShort)base).func_150289_e();
		if(base instanceof NBTTagInt)
			return ((NBTTagInt)base).func_150287_d();
		if(base instanceof NBTTagLong)
			return ((NBTTagLong)base).func_150291_c();
		if(base instanceof NBTTagFloat)
			return ((NBTTagFloat)base).func_150288_h();
		if(base instanceof NBTTagDouble)
			return ((NBTTagDouble)base).func_150286_g();
		if(base instanceof NBTTagByteArray)
			return ((NBTTagByteArray)base).func_150292_c();
		if(base instanceof NBTTagString)
			return ((NBTTagString)base).func_150285_a_();
		if(base instanceof NBTTagList)
			return base;
		if(base instanceof NBTTagCompound)
			return ((NBTTagCompound)base);
		if(base instanceof NBTTagIntArray)
			return ((NBTTagIntArray)base).func_150302_c();
		return null;
	}
	
	@Override
	public String toString(Object object) {
		NBTTagCompound nbt = (NBTTagCompound) object;
		Set tags = nbt.func_150296_c();
		Iterator iterator = tags.iterator();
		ArrayList objects = new ArrayList();
		int i = 0;
		while(iterator.hasNext())
		{
			Object ObjectString = iterator.next();
			if(ObjectString instanceof String)
			{
				String name = (String) ObjectString;
				NBTBase base = nbt.getTag(name);
				Object data = getObject(base);
				
				if(data == null)
					System.out.println("Found invalid NBT Data (END is unspported)");
				else
				{
					objects.add(name);
					objects.add(data);
				}
				i++;
			}
		}
		return StringUtils.ObjectsToString(objects.toArray());
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < objects.length/2; i++) {
			int index = i*2;
			String name = (String)objects[index];
			Object object = objects[index+1];
			if(object instanceof Byte[])
				nbt.setByteArray(name, ArrayUtils.toPrimitive((Byte[])object));
			else if(object instanceof byte[])
				nbt.setByteArray(name, (byte[]) object);
			else if(object instanceof Integer[])
				nbt.setIntArray(name, ArrayUtils.toPrimitive((Integer[])object));
			else if(object instanceof int[])
				nbt.setIntArray(name, (int[]) object);
			else if(object instanceof Byte)
				nbt.setByte(name, (Byte)object);
			else if(object instanceof Short)
				nbt.setShort(name, (Short)object);
			else if(object instanceof Integer)
				nbt.setInteger(name, (Integer)object);
			else if(object instanceof Long)
				nbt.setLong(name, (Long)object);
			else if(object instanceof Float)
				nbt.setFloat(name, (Float)object);
			else if(object instanceof Double)
				nbt.setDouble(name, (Double)object);
			else if(object instanceof String)
				nbt.setString(name, (String)object);
			else if(object instanceof NBTTagCompound)
				nbt.setTag(name, (NBTTagCompound)object);
			else if(object instanceof NBTTagList)
				nbt.setTag(name, (NBTTagList) object);
			else
			{
				System.out.println("Couldn't add object to nbttag name=" + name + " class=" + object.getClass().getName());
			}
		}
		return nbt;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
