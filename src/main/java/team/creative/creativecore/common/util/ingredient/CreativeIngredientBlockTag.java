package team.creative.creativecore.common.util.ingredient;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;

public class CreativeIngredientBlockTag extends CreativeIngredient {
    
    public TagKey<Block> tag;
    
    public CreativeIngredientBlockTag(TagKey<Block> tag) {
        this.tag = tag;
    }
    
    public CreativeIngredientBlockTag() {}
    
    @Override
    protected void saveExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        nbt.putString("tag", tag.location().toString());
    }
    
    @Override
    protected void loadExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(nbt.getString("tag")));
    }
    
    @Override
    public boolean is(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        if (!(Block.byItem(stack.getItem()) instanceof AirBlock))
            return block.builtInRegistryHolder().is(tag);
        return false;
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) info).tag == tag;
    }
    
    @Override
    public ItemStack getExample() {
        Optional<Named<Block>> optional = BuiltInRegistries.BLOCK.getTag(tag);
        if (optional.isEmpty() || optional.get().size() == 0)
            return ItemStack.EMPTY;
        return new ItemStack(optional.get().get(0).value());
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientBlockTag && ((CreativeIngredientBlockTag) object).tag == tag;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientBlockTag(tag);
    }
    
    @Override
    public Component description() {
        return Component.literal(tag.location().toString());
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.block_tag").append(": " + ChatFormatting.YELLOW + tag.location());
    }
    
}
