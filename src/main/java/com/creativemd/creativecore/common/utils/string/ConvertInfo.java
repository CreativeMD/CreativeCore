package com.creativemd.creativecore.common.utils.string;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.nbt.NBTTagCompound;

import com.creativemd.creativecore.common.utils.stack.IStackLoader;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.creativecore.common.utils.stack.StackInfoFuel;

public class ConvertInfo extends StringConverter{

	public ConvertInfo() {
		super("StackInfo");
	}

	@Override
	public Class getClassOfObject() {
		return StackInfo.class;
	}

	@Override
	public String toString(Object object) {
		StackInfo info = (StackInfo) object;
		return StringUtils.ObjectsToString(info.getClass().getName().replace("com.creativemd.creativecore.common.utils.stack", "$d$"), info.toString(), info.stackSize);
	}

	@Override
	public Object parseObject(String input) {
		Object[] objects = StringUtils.StringToObjects(input);
		if(objects.length == 3)
		{
			String className = ((String) objects[0]).replace("$d$", "com.creativemd.creativecore.common.utils.stack");
			IStackLoader loader = null;
			for (int i = 0; i < StackInfo.loaders.size(); i++) {
				if(StackInfo.loaders.get(i).getClass().getName().equals(className))
					loader = StackInfo.loaders.get(i);
			}
			StackInfo info = null;
			if(loader != null)
			{
				info = loader.getStackInfoFromString((String) objects[1]);
			}else{
				try {
					info = (StackInfo) Class.forName(className).getConstructor().newInstance();
				} catch (Exception e) {
					System.out.println("Invalid StackInfo! name=" + className + ". Misses empty constructor");
				}
			}
			if(info != null)
				info.stackSize = (Integer) objects[2];
			return info;
		}
		return null;
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
