package com.creativemd.creativecore.common.utils.string;

import java.util.ArrayList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

public class ConvertNBTTagList extends StringConverter{

	public ConvertNBTTagList() {
		super("NBTTagList");
	}

	@Override
	public Class getClassOfObject() {
		return NBTTagList.class;
	}

	@Override
	public String toString(Object object) {
		ArrayList objects = new ArrayList();
		NBTTagList list = (NBTTagList) object;
		for (int i = 0; i < list.tagCount(); i++) {
			Object data = ConvertNBTTagCompound.getObject(list.getCompoundTagAt(i));
			if(data == null)
				System.out.println("Found invalid NBT Data (END is unspported)");
			else
				objects.add(data);
		}
		return StringUtils.ObjectsToString(objects.toArray());
	}

	@Override
	public Object parseObject(String input) {
		NBTTagList list = new NBTTagList();
		Object[] objects = StringUtils.StringToObjects(input);
		for (int i = 0; i < objects.length; i++) {
			if(objects[i] instanceof NBTBase)
				list.appendTag((NBTBase) objects[i]);
		}
		return list;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
