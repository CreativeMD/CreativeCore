package team.creative.creativecore.common.util.ingredient;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CreativeIngredientItemTag extends CreativeIngredient {
    
    public TagKey<Item> tag;
    
    public CreativeIngredientItemTag(TagKey<Item> tag) {
        this.tag = tag;
    }
    
    public CreativeIngredientItemTag() {}
    
    @Override
    protected void saveExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        nbt.putString("tag", tag.location().toString());
    }
    
    @Override
    protected void loadExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(nbt.getStringOr("tag", "")));
    }
    
    @Override
    public boolean is(Level level, ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().is(tag);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientItemTag && ((CreativeIngredientItemTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        var itr = BuiltInRegistries.ITEM.getTagOrEmpty(tag).iterator();
        if (!itr.hasNext())
            return ItemStack.EMPTY;
        return new ItemStack(itr.next().value());
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
