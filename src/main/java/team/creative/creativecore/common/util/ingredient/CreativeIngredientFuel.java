package team.creative.creativecore.common.util.ingredient;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class CreativeIngredientFuel extends CreativeIngredient {
    
    public CreativeIngredientFuel() {}
    
    @Override
    protected void saveExtra(HolderLookup.Provider provider, CompoundTag nbt) {}
    
    @Override
    protected void loadExtra(HolderLookup.Provider provider, CompoundTag nbt) {}
    
    @Override
    public ItemStack getExample() {
        return new ItemStack(Items.COAL);
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return AbstractFurnaceBlockEntity.isFuel(stack);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        return info instanceof CreativeIngredientFuel;
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientFuel;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientFuel();
    }
    
    @Override
    public Component description() {
        return descriptionDetail();
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.fuel");
    }
    
}
