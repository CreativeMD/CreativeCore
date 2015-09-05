package com.creativemd.creativecore.common.utils.string;

import net.minecraft.item.Item;

public class ConvertItem extends StringConverter{

	public ConvertItem() {
		super("Item");
	}

	@Override
	public Class getClassOfObject() {
		return Item.class;
	}

	@Override
	public String toString(Object object) {
		return Item.itemRegistry.getNameForObject(object);
	}

	@Override
	public Object parseObject(String input) {
		return Item.itemRegistry.getObject(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
