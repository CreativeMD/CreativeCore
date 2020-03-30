package team.creative.creativecore.common.util.ingredient;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CreativeIngredientMaterial extends CreativeIngredient {
	
	public Material material;
	
	public CreativeIngredientMaterial(Material material) {
		this.material = material;
	}
	
	public CreativeIngredientMaterial() {
		super();
	}
	
	@Override
	protected void writeExtra(CompoundNBT nbt) {
		nbt.putString("material", getBlock().getRegistryName().toString());
	}
	
	@Override
	protected void readExtra(CompoundNBT nbt) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("material")));
		if (block != null)
			material = block.getDefaultState().getMaterial();
	}
	
	public Block getBlock() {
		for (Block block : ForgeRegistries.BLOCKS)
			if (block.getDefaultState().getMaterial() == material)
				return block;
		return null;
	}
	
	@Override
	public ItemStack getExample() {
		return new ItemStack(getBlock());
	}
	
	public static BlockState getState(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block != null)
			return block.getDefaultState();
		return null;
	}
	
	@Override
	public boolean is(CreativeIngredient info) {
		return info instanceof CreativeIngredientMaterial && ((CreativeIngredientMaterial) info).material == this.material;
	}
	
	@Override
	public boolean is(ItemStack stack) {
		BlockState state = getState(stack);
		if (state != null)
			return state.getMaterial() == this.material;
		return false;
	}
	
	@Override
	public boolean equals(CreativeIngredient object) {
		return object instanceof CreativeIngredientMaterial && ((CreativeIngredientMaterial) object).material == this.material;
	}
}
