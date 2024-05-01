package team.creative.creativecore.common.util.ingredient;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
        tag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(nbt.getString("tag")));
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().is(tag);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        Optional<Named<Item>> optional = Registry.ITEM.getTag(tag);
        if (optional.isEmpty() || optional.get().size() == 0)
            return ItemStack.EMPTY;
        return new ItemStack(optional.get().get(0).value());
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) object).tag == tag;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItemTag(tag);
    }
    
    @Override
    public Component description() {
        return Component.literal(tag.location().toString());
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.item_tag").append(": " + ChatFormatting.YELLOW + tag.location());
    }
    
}
