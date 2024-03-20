package net.minecraftforge.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

public interface IItemHandler {
    
    int getSlots();
    
    @NotNull
    ItemStack getStackInSlot(int slot);
    
    @NotNull
    ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);
    
    @NotNull
    ItemStack extractItem(int slot, int amount, boolean simulate);
    
    int getSlotLimit(int slot);
    
    boolean isItemValid(int slot, @NotNull ItemStack stack);
}
