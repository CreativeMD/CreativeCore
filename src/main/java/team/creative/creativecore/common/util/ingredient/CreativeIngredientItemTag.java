package team.creative.creativecore.common.util.ingredient;

import java.util.Optional;

import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreativeIngredientItemTag extends CreativeIngredient {
    
    public TagKey<Item> tag;
    
    public CreativeIngredientItemTag(TagKey<Item> tag) {
        this.tag = tag;
    }
    
    public CreativeIngredientItemTag() {
        
    }
    
    @Override
    protected void saveExtra(CompoundTag nbt) {
        nbt.putString("tag", tag.location().toString());
    }
    
    @Override
    protected void loadExtra(CompoundTag nbt) {
        tag = ItemTags.create(new ResourceLocation(nbt.getString("tag")));
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean is(ItemStack stack) {
        return Registry.ITEM.getHolderOrThrow(Registry.ITEM.getResourceKey(stack.getItem()).get()).is(tag);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        @SuppressWarnings("deprecation")
        Optional<Named<Item>> optional = Registry.ITEM.getTag(tag);
        if (optional.isEmpty() || optional.get().size() == 0)
            return ItemStack.EMPTY;
        return new ItemStack(optional.get().iterator().next());
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) object).tag == tag;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItemTag(tag);
    }
}
