package team.creative.creativecore.common.util.ingredient;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class CreativeIngredientBlockTag extends CreativeIngredient {

	public ITag<Block> tag;

	public CreativeIngredientBlockTag(Tag<Block> tag) {
		this.tag = tag;
	}

	public CreativeIngredientBlockTag() {

	}

	@Override
	protected void writeExtra(CompoundNBT nbt) {
		nbt.putString("tag", BlockTags.getCollection().func_232975_b_(tag).toString());
	}

	@Override
	protected void readExtra(CompoundNBT nbt) {
		tag = BlockTags.getCollection().get(new ResourceLocation(nbt.getString("tag")));
	}

	@Override
	public boolean is(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block != null)
			return tag.func_230235_a_(block);
		return false;
	}

	@Override
	public boolean is(CreativeIngredient info) {
		return info instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) info).tag == tag;
	}

	@Override
	public ItemStack getExample() {
		if (tag.func_230236_b_().isEmpty())
			return ItemStack.EMPTY;
		return new ItemStack(tag.func_230236_b_().iterator().next());
	}

	@Override
	public boolean equals(CreativeIngredient object) {
		return object instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) object).tag == tag;
	}

}
