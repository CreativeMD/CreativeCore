package team.creative.creativecore.common.util.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;

public class CreativeIngredientFuel extends CreativeIngredient {
    
    public CreativeIngredientFuel() {
        
    }
    
    @Override
    protected void writeExtra(CompoundNBT nbt) {
        
    }
    
    @Override
    protected void readExtra(CompoundNBT nbt) {
        
    }
    
    @Override
    public ItemStack getExample() {
        return new ItemStack(Items.COAL);
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return AbstractFurnaceTileEntity.isFuel(stack);
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
    
}
