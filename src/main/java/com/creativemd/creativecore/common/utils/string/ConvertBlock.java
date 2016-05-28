package com.creativemd.creativecore.common.utils.string;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

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
		return Block.REGISTRY.getNameForObject((Block)object).toString();
	}

	@Override
	public Object parseObject(String input) {
		return Block.REGISTRY.getObject(new ResourceLocation(input));
	}

	@Override
	public String[] getSplitter() {
		return new String[0];
	}

}
