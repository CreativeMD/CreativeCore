package team.creative.creativecore.common.util.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CreativeIngredientBlock extends CreativeIngredient {
    
    public Block block;
    
    public CreativeIngredientBlock(Block block) {
        this.block = block;
    }
    
    public CreativeIngredientBlock() {
        super();
    }
    
    @Override
    protected void loadExtra(CompoundTag nbt) {
        nbt.putString("block", block.getRegistryName().toString());
    }
    
    @Override
    protected void saveExtra(CompoundTag nbt) {
        block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("block")));
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        if (info instanceof CreativeIngredientBlock)
            return ((CreativeIngredientBlock) info).block == block;
        if (info instanceof CreativeIngredientItemStack)
            return block == Block.byItem(((CreativeIngredientItemStack) info).stack.getItem());
        return false;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return Block.byItem(stack.getItem()) == this.block;
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientBlock && ((CreativeIngredientBlock) object).block == this.block;
    }
    
    @Override
    public ItemStack getExample() {
        return new ItemStack(block);
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientBlock(block);
    }
    
}
