package com.creativemd.creativecore.common.utils.string;

import net.minecraft.block.Block;

public class ConvertBlock extends StringConverter{

	public ConvertBlock() {
		super("Block");
	}

	@Override
	public Class getClassOfObject() {
		return Block.class;
	}

	@Override
	public String toString(Object object) {
		return Block.blockRegistry.getNameForObject(object);
	}

	@Override
	public Object parseObject(String input) {
		return Block.blockRegistry.getObject(input);
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
