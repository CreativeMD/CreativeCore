package team.creative.creativecore.common.util.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CreativeIngredientItemStack extends CreativeIngredient {
    
    public ItemStack stack;
    
    public CreativeIngredientItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    public CreativeIngredientItemStack() {
        super();
    }
    
    @Override
    protected void writeExtra(CompoundNBT nbt) {
        stack.write(nbt);
    }
    
    @Override
    protected void readExtra(CompoundNBT nbt) {
        stack = ItemStack.read(nbt);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        if (info instanceof CreativeIngredientItemStack) {
            if (((CreativeIngredientItemStack) info).stack.getItem() != stack.getItem())
                return false;
            
            if (((CreativeIngredientItemStack) info).stack.getDamage() != stack.getDamage())
                return false;
            
            if (!ItemStack.areItemStackTagsEqual(((CreativeIngredientItemStack) info).stack, stack))
                return false;
            
            return true;
        }
        return false;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        if (stack.getItem() != this.stack.getItem())
            return false;
        
        if (stack.getDamage() != this.stack.getDamage())
            return false;
        
        if (!ItemStack.areItemStackTagsEqual(this.stack, stack))
            return false;
        
        return true;
    }
    
    @Override
    public ItemStack getExample() {
        return stack.copy();
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItemStack && ((CreativeIngredientItemStack) object).is(stack);
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItemStack(stack.copy());
    }
}
