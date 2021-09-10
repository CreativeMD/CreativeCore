package team.creative.creativecore.common.util.ingredient;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiInfoStackButton;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.gui.GuiParent;

public abstract class CreativeIngredient {
    
    private static HashMap<String, Class<? extends CreativeIngredient>> types = new HashMap<>();
    private static HashMap<Class<? extends CreativeIngredient>, String> typesInv = new HashMap<>();
    private static List<Function<Object, ? extends CreativeIngredient>> objectParsers = new ArrayList<>();
    
    public static <T extends CreativeIngredient> void registerType(String id, Class<T> classType, Function<Object, T> parser) {
        if (types.containsKey(id))
            throw new IllegalArgumentException("Id '" + id + "' is already taken");
        
        try {
            classType.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new InvalidParameterException("The class does not contain an empty constructor");
        }
        
        types.put(id, classType);
        typesInv.put(classType, id);
        if (parser != null)
            objectParsers.add(parser);
    }
    
    public static String getId(CreativeIngredient ingredient) {
        return getId(ingredient.getClass());
    }
    
    public static String getId(Class<? extends CreativeIngredient> clazz) {
        return typesInv.get(clazz);
    }
    
    public static Class<? extends CreativeIngredient> getClass(String id) {
        return types.get(id);
    }
    
    public static CreativeIngredient parse(Object object) {
        if (object == null)
            return null;
        if (object instanceof CreativeIngredient)
            return (CreativeIngredient) object;
        
        for (int i = 0; i < objectParsers.size(); i++)
            try {
                CreativeIngredient ingredient = objectParsers.get(i).apply(object);
                if (ingredient != null)
                    return ingredient;
            } catch (Exception e) {}
        
        return null;
    }
    
    public static CreativeIngredient read(CompoundTag nbt) {
        Class<? extends CreativeIngredient> classType = getClass(nbt.getString("id"));
        if (classType == null)
            throw new IllegalArgumentException("'" + nbt.getString("id") + "' is an invalid type");
        
        try {
            CreativeIngredient ingredient = classType.getConstructor().newInstance();
            ingredient.readExtra(nbt);
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
            if (x instanceof Tag && !((Tag) x).getValues().isEmpty() && ((Tag) x).getValues().iterator().next() instanceof Block)
                return new CreativeIngredientBlockTag((Tag<Block>) x);
            return null;
        });
        
        registerType("item", CreativeIngredientItem.class, (x) -> {
            if (x instanceof Item && !(x instanceof BlockItem))
                return new CreativeIngredientItem((Item) x);
            return null;
        });
        registerType("itemtag", CreativeIngredientItemTag.class, (x) -> {
            if (x instanceof Tag && !((Tag) x).getValues().isEmpty() && ((Tag) x).getValues().iterator().next() instanceof Item)
                return new CreativeIngredientItemTag((Tag<Item>) x);
            return null;
        });
        
        registerType("itemstack", CreativeIngredientItemStack.class, (x) -> x instanceof ItemStack ? new CreativeIngredientItemStack((ItemStack) x, false) : null);
        
        registerType("material", CreativeIngredientMaterial.class, (x) -> x instanceof Material ? new CreativeIngredientMaterial((Material) x) : null);
        registerType("fuel", CreativeIngredientFuel.class, null);
        
        final CreativeIngredient temp = new CreativeIngredientBlock(Blocks.DIRT);
        
        ConfigTypeConveration.registerSpecialType((x) -> CreativeIngredient.class.isAssignableFrom(x), new ConfigTypeConveration.SimpleConfigTypeConveration<CreativeIngredient>() {
            
            @Override
            public CreativeIngredient readElement(CreativeIngredient defaultValue, boolean loadDefault, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    try {
                        return CreativeIngredient.read(TagParser.parseTag(element.getAsString()));
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(CreativeIngredient value, CreativeIngredient defaultValue, boolean saveDefault) {
                return new JsonPrimitive(value.write(new CompoundTag()).toString());
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {
                parent.add(new GuiInfoStackButton("data", temp).setExpandable());
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(CreativeIngredient value, GuiParent parent) {
                GuiInfoStackButton button = (GuiInfoStackButton) parent.get("data");
                button.set(value);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected CreativeIngredient saveValue(GuiParent parent, Class clazz) {
                GuiInfoStackButton button = (GuiInfoStackButton) parent.get("data");
                return button.get();
            }
            
            @Override
            public CreativeIngredient set(ConfigKeyField key, CreativeIngredient value) {
                return value;
            }
        });
    }
    
    public CreativeIngredient() {
        
    }
    
    public CompoundTag write(CompoundTag nbt) {
        nbt.putString("id", getId(this));
        writeExtra(nbt);
        return nbt;
    }
    
    protected abstract void writeExtra(CompoundTag nbt);
    
    protected abstract void readExtra(CompoundTag nbt);
    
    public abstract boolean is(ItemStack stack);
    
    public abstract boolean is(CreativeIngredient info);
    
    public abstract ItemStack getExample();
    
    public abstract CreativeIngredient copy();
    
    @Override
    public boolean equals(Object object) {
        return object instanceof CreativeIngredient && equals((CreativeIngredient) object);
    }
    
    public abstract boolean equals(CreativeIngredient object);
    
}
