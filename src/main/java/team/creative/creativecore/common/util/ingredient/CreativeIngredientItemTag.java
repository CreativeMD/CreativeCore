package team.creative.creativecore.common.util.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreativeIngredientItemTag extends CreativeIngredient {
    
    public Tag<Item> tag;
    
    public CreativeIngredientItemTag(Tag<Item> tag) {
        this.tag = tag;
    }
    
    public CreativeIngredientItemTag() {
        
    }
    
    @Override
    protected void writeExtra(CompoundTag nbt) {
        nbt.putString("tag", ItemTags.getAllTags().getId(tag).toString());
    }
    
    @Override
    protected void readExtra(CompoundTag nbt) {
        tag = ItemTags.getAllTags().getTag(new ResourceLocation(nbt.getString("tag")));
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return tag.contains(stack.getItem());
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        if (tag.getValues().isEmpty())
            return ItemStack.EMPTY;
        return new ItemStack(tag.getValues().iterator().next());
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
