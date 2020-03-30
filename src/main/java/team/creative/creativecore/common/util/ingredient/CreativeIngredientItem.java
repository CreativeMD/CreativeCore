package team.creative.creativecore.common.util.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CreativeIngredientItem extends CreativeIngredient {
	
	public Item item;
	
	public CreativeIngredientItem(Item item) {
		this.item = item;
	}
	
	public CreativeIngredientItem() {
		super();
	}
	
	@Override
	protected void writeExtra(CompoundNBT nbt) {
		nbt.putString("item", item.getRegistryName().toString());
	}
	
	@Override
	protected void readExtra(CompoundNBT nbt) {
		item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("item")));
	}
	
	@Override
	public ItemStack getExample() {
		return new ItemStack(item);
	}
	
	@Override
	public boolean is(CreativeIngredient info) {
		if (info instanceof CreativeIngredientItem)
			return ((CreativeIngredientItem) info).item == item;
		if (info instanceof CreativeIngredientItemStack)
			return item == ((CreativeIngredientItemStack) info).stack.getItem();
		return false;
	}
	
	@Override
	public boolean is(ItemStack stack) {
		return stack.getItem() == item;
	}
	
	@Override
	public boolean equals(CreativeIngredient object) {
		return object instanceof CreativeIngredientItem && ((CreativeIngredientItem) object).item == this.item;
	}
	
}
