package team.creative.creativecore.common.util.ingredient;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

            return ItemStack.isSameItem(((CreativeIngredientItemStack) info).stack, stack);
        }
        return false;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        if (stack.getItem() != this.stack.getItem())
            return false;
        
        if (stack.getDamageValue() != this.stack.getDamageValue())
            return false;

        return ItemStack.isSameItem(this.stack, stack);
    }
    
    @Override
    public ItemStack getExample() {
        return stack.copy();
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItemStack && object.is(stack);
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItemStack(stack.copy(), needNBT);
    }
    
    @Override
    public Component description() {
        return stack.getDisplayName();
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.stack").append(": " + ChatFormatting.YELLOW + stack.getDisplayName());
    }
    
}
