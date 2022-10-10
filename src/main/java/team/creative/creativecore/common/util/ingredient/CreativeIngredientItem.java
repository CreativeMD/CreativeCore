package team.creative.creativecore.common.util.ingredient;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreativeIngredientItem extends CreativeIngredient {
    
    public Item item;
    
    public CreativeIngredientItem(Item item) {
        this.item = item;
    }
    
    public CreativeIngredientItem() {
        super();
    }
    
    @Override
    protected void saveExtra(CompoundTag nbt) {
        nbt.putString("item", Registry.ITEM.getKey(item).toString());
    }
    
    @Override
    protected void loadExtra(CompoundTag nbt) {
        item = Registry.ITEM.get(new ResourceLocation(nbt.getString("item")));
    }
    
    @Override
    public ItemStack getExample() {
        return new ItemStack(item);
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        if (info instanceof CreativeIngredientItem)
            return ((CreativeIngredientItem) info).item == item;
        if (info instanceof CreativeIngredientItemStack)
            return item == ((CreativeIngredientItemStack) info).stack.getItem();
        return false;
    }
    
    @Override
    public boolean is(ItemStack stack) {
        return stack.getItem() == item;
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItem && ((CreativeIngredientItem) object).item == this.item;
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItem(item);
    }
    
    @Override
    public Component description() {
        return Component.translatable(item.getDescriptionId());
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.item").append(": " + ChatFormatting.YELLOW + Registry.ITEM.getKey(item).toString());
    }
    
}
