package team.creative.creativecore.common.util.ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class CreativeIngredientItemStack extends CreativeIngredient {
    
    public ItemStack stack;
    private List<ResourceLocation> included = Collections.EMPTY_LIST;
    
    public CreativeIngredientItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    public CreativeIngredientItemStack(ItemStack stack, List<ResourceLocation> included) {
        this.stack = stack;
        this.included = included;
    }
    
    public CreativeIngredientItemStack() {
        super();
    }
    
    public List<ResourceLocation> getIncluded() {
        return new ArrayList<>(included);
    }
    
    @Override
    protected void saveExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        nbt.put("stack", stack.saveOptional(provider));
        if (!included.isEmpty())
            nbt.putString("included", String.join(";", new FunctionIterator<String>(included, x -> x.toString())));
    }
    
    @Override
    protected void loadExtra(HolderLookup.Provider provider, CompoundTag nbt) {
        stack = ItemStack.parseOptional(provider, nbt.getCompound("stack"));
        if (nbt.contains("included")) {
            included = new ArrayList<>();
            var array = nbt.getString("included").split(";");
            for (int i = 0; i < array.length; i++)
                included.add(ResourceLocation.parse(array[i]));
        }
    }
    
    @Override
    public boolean is(CreativeIngredient info) {
        if (info instanceof CreativeIngredientItemStack s)
            return is(null, s.stack);
        return false;
    }
    
    @Override
    public boolean is(Level level, ItemStack stack) {
        if (stack.getItem() != this.stack.getItem())
            return false;
        
        for (TypedDataComponent<?> comp : this.stack.getComponents())
            if (included.contains(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.type())) && !Objects.equals(comp.value(), stack.get(comp.type())))
                return false;
            
        return true;
    }
    
    @Override
    public ItemStack getExample() {
        return stack.copy();
    }
    
    @Override
    public boolean equals(CreativeIngredient object) {
        return object instanceof CreativeIngredientItemStack && object.is(null, stack);
    }
    
    @Override
    public CreativeIngredient copy() {
        return new CreativeIngredientItemStack(stack.copy());
    }
    
    @Override
    public Component description() {
        return stack.getDisplayName();
    }
    
    @Override
    public Component descriptionDetail() {
        return Component.translatable("minecraft.stack").append(": ").append(((MutableComponent) stack.getDisplayName()).withStyle(ChatFormatting.YELLOW));
    }
    
}
