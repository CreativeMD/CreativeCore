package team.creative.creativecore.common.util.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class CreativeIngredientItemTag extends CreativeIngredient {

	public ITag<Item> tag;

	public CreativeIngredientItemTag(Tag<Item> tag) {
		this.tag = tag;
	}

	public CreativeIngredientItemTag() {

	}

	@Override
	protected void writeExtra(CompoundNBT nbt) {
		nbt.putString("tag", ItemTags.getCollection().func_232975_b_(tag).toString());
	}

	@Override
	protected void readExtra(CompoundNBT nbt) {
		tag = ItemTags.getCollection().get(new ResourceLocation(nbt.getString("tag")));
	}

	@Override
	public boolean is(ItemStack stack) {
		return tag.func_230235_a_(stack.getItem());
	}

	@Override
	public boolean is(CreativeIngredient info) {
		return info instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) info).tag == tag;
	}

	@Override
	public ItemStack getExample() {
		if (tag.func_230236_b_().isEmpty())
			return ItemStack.EMPTY;
		return new ItemStack(tag.func_230236_b_().iterator().next());
	}

	@Override
	public boolean equals(CreativeIngredient object) {
		return object instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) object).tag == tag;
	}

}
