package team.creative.creativecore.common.util.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CreativeIngredientItemStack extends CreativeIngredient {
    
    public ItemStack stack;
    public boolean needNBT;
    
    public CreativeIngredientItemStack(ItemStack stack, boolean needNBT) {
        this.stack = stack;
        this.needNBT = needNBT;
    }
    
    public CreativeIngredientItemStack() {
        super();
    }
    
    @Override
    protected void saveExtra(CompoundTag nbt) {
        stack.save(nbt);
        nbt.putBoolean("needNBT", needNBT);
    }
    
    @Override
    protected void loadExtra(CompoundTag nbt) {
        stack = ItemStack.of(nbt);
        needNBT = nbt.getBoolean("needNBT");
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        if (info instanceof CreativeIngredientItemStack) {
            if (((CreativeIngredientItemStack) info).stack.getItem() != stack.getItem())
                return false;
            
            if (((CreativeIngredientItemStack) info).stack.getDamageValue() != stack.getDamageValue())
                return false;
            
            if (!ItemStack.isSame(((CreativeIngredientItemStack) info).stack, stack))
                return false;
            
            return true;
        }
        return false;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        if (stack.getItem() != this.stack.getItem())
            return false;
        
        if (stack.getDamageValue() != this.stack.getDamageValue())
            return false;
        
        if (!ItemStack.isSame(this.stack, stack))
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
        return new CreativeIngredientItemStack(stack.copy(), needNBT);
    }
}
