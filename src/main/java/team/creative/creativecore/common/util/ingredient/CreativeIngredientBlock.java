package team.creative.creativecore.common.util.ingredient;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CreativeIngredientBlock extends CreativeIngredient {
	
	public Block block;
	
	public CreativeIngredientBlock(Block block) {
		this.block = block;
	}
	
	public CreativeIngredientBlock() {
		super();
	}
	
	@Override
	protected void writeExtra(CompoundNBT nbt) {
		nbt.putString("block", block.getRegistryName().toString());
	}
	
	@Override
	protected void readExtra(CompoundNBT nbt) {
		block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("block")));
	}
	
	@Override
	public boolean is(CreativeIngredient info) {
		if (info instanceof CreativeIngredientBlock)
			return ((CreativeIngredientBlock) info).block == block;
		if (info instanceof CreativeIngredientItemStack)
			return block == Block.getBlockFromItem(((CreativeIngredientItemStack) info).stack.getItem());
		return false;
	}
	
	@Override
	public boolean is(ItemStack stack) {
		return Block.getBlockFromItem(stack.getItem()) == this.block;
	}
	
	@Override
	public boolean equals(CreativeIngredient object) {
		return object instanceof CreativeIngredientBlock && ((CreativeIngredientBlock) object).block == this.block;
	}
	
	@Override
	public ItemStack getExample() {
		return new ItemStack(block);
	}
	
}
