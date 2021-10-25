package team.creative.creativecore.common.util.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CreativeIngredientBlockTag extends CreativeIngredient {
    
    public Tag<Block> tag;
    
    public CreativeIngredientBlockTag(Tag<Block> tag) {
        this.tag = tag;
    }
    
    public CreativeIngredientBlockTag() {
        
    }
    
    @Override
    protected void saveExtra(CompoundTag nbt) {
        nbt.putString("tag", BlockTags.getAllTags().getId(tag).toString());
    }
    
    @Override
    protected void loadExtra(CompoundTag nbt) {
        tag = BlockTags.getAllTags().getTag(new ResourceLocation(nbt.getString("tag")));
    }
    
    @Override
    public boolean is(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        if (block != null)
            return tag.contains(block);
        return false;
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        if (tag.getValues().isEmpty())
            return ItemStack.EMPTY;
        return new ItemStack(tag.getValues().iterator().next());
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) object).tag == tag;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientBlockTag(tag);
    }
    
}
