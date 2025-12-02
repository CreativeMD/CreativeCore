package team.creative.creativecore.common.util.ingredient;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;

public abstract class CreativeIngredient {
    
    public static final NamedTypeRegistry<CreativeIngredient> REGISTRY = new NamedTypeRegistry<CreativeIngredient>().addConstructorPattern();
    private static final List<Function<Object, ? extends CreativeIngredient>> OBJECT_PARSERS = new ArrayList<>();
    private static final CreativeIngredient EMPTY = new CreativeIngredient() {
        
        @Override
        protected void saveExtra(HolderLookup.Provider provider, CompoundTag nbt) {}
        
        @Override
        protected void loadExtra(HolderLookup.Provider provider, CompoundTag nbt) {}
        
        @Override
        public boolean is(CreativeIngredient info) {
            return false;
        }
        
        @Override
        public boolean is(Level level, ItemStack stack) {
            return false;
        }
        
        @Override
        public ItemStack getExample() {
            return ItemStack.EMPTY;
        }
        
        @Override
        public boolean equals(CreativeIngredient object) {
            return false;
        }
        
        @Override
        public Component descriptionDetail() {
            return Component.literal("empty");
        }
        
        @Override
        public Component description() {
            return Component.literal("invalid");
        }
        
        @Override
        public CreativeIngredient copy() {
            return EMPTY;
        }
    };
    
    public static <T extends CreativeIngredient> void registerType(String id, Class<T> classType, Function<Object, T> parser) {
        REGISTRY.register(id, classType);
        if (parser != null)
            OBJECT_PARSERS.add(parser);
    }
    
    public static CreativeIngredient parse(Object object) {
        if (object == null)
            return null;
        if (object instanceof CreativeIngredient)
            return (CreativeIngredient) object;
        
        for (int i = 0; i < OBJECT_PARSERS.size(); i++)
            try {
                CreativeIngredient ingredient = OBJECT_PARSERS.get(i).apply(object);
                if (ingredient != null)
                    return ingredient;
            } catch (Exception e) {}
        
        return null;
    }
    
    public static CreativeIngredient load(HolderLookup.Provider provider, CompoundTag nbt) {
        Class<? extends CreativeIngredient> classType = REGISTRY.get(nbt.getStringOr("id", ""));
        if (classType == null) {
            LOGGER.error(new IllegalArgumentException("'" + nbt.getString("id") + "' is an invalid type"));
            return EMPTY;
        }
        
        try {
            CreativeIngredient ingredient = classType.getConstructor().newInstance();
            ingredient.loadExtra(provider, nbt);
            return ingredient;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        // Load default types
        registerType("block", CreativeIngredientBlock.class, (x) -> {
            Block block = null;
            if (x instanceof Block)
                block = (Block) x;
            if (x instanceof BlockItem)
                block = Block.byItem((Item) x);
            
            if (block != null && !(block instanceof AirBlock))
                return new CreativeIngredientBlock(block);
            return null;
        });
        registerType("blocktag", CreativeIngredientBlockTag.class, (x) -> {
            if (x instanceof TagKey key && key.isFor(Registries.BLOCK))
                return new CreativeIngredientBlockTag((TagKey<Block>) x);
            return null;
        });
        
        registerType("item", CreativeIngredientItem.class, (x) -> {
            if (x instanceof Item && !(x instanceof BlockItem))
                return new CreativeIngredientItem((Item) x);
            return null;
        });
        registerType("itemtag", CreativeIngredientItemTag.class, (x) -> {
            if (x instanceof TagKey key && key.isFor(Registries.ITEM))
                return new CreativeIngredientItemTag((TagKey<Item>) x);
            return null;
        });
        
        registerType("itemstack", CreativeIngredientItemStack.class, (x) -> x instanceof ItemStack s ? new CreativeIngredientItemStack(s) : null);
        registerType("fuel", CreativeIngredientFuel.class, null);
        
    }
    
    public CreativeIngredient() {}
    
    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", REGISTRY.getIdOrDefault(this, "empty"));
        saveExtra(provider, nbt);
        return nbt;
    }
    
    protected abstract void saveExtra(HolderLookup.Provider provider, CompoundTag nbt);
    
    protected abstract void loadExtra(HolderLookup.Provider provider, CompoundTag nbt);
    
    public abstract boolean is(Level level, ItemStack stack);
    
    public abstract boolean is(CreativeIngredient info);
    
    public abstract ItemStack getExample();
    
    public abstract CreativeIngredient copy();
    
    @Override
    public boolean equals(Object object) {
        return object instanceof CreativeIngredient c && equals(c);
    }
    
    public abstract boolean equals(CreativeIngredient object);
    
    public abstract Component description();
    
    public abstract Component descriptionDetail();
    
}
