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

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.Tag;
import team.creative.creativecore.common.config.ConfigTypeConveration;

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
			} catch (Exception e) {
				
			}
		
		return null;
	}
	
	public static CreativeIngredient read(CompoundNBT nbt) {
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
				block = Block.getBlockFromItem((Item) x);
			
			if (block != null && !(block instanceof AirBlock))
				return new CreativeIngredientBlock(block);
			return null;
		});
		registerType("blocktag", CreativeIngredientBlockTag.class, (x) -> {
			if (x instanceof Tag && !((Tag) x).func_230236_b_().isEmpty() && ((Tag) x).func_230236_b_().iterator().next() instanceof Block)
				return new CreativeIngredientBlockTag((Tag<Block>) x);
			return null;
		});
		
		registerType("item", CreativeIngredientItem.class, (x) -> {
			if (x instanceof Item && !(x instanceof BlockItem))
				return new CreativeIngredientItem((Item) x);
			return null;
		});
		registerType("itemtag", CreativeIngredientItemTag.class, (x) -> {
			if (x instanceof Tag && !((Tag) x).func_230236_b_().isEmpty() && ((Tag) x).func_230236_b_().iterator().next() instanceof Item)
				return new CreativeIngredientItemTag((Tag<Item>) x);
			return null;
		});
		
		registerType("itemstack", CreativeIngredientItemStack.class, (x) -> x instanceof ItemStack ? new CreativeIngredientItemStack((ItemStack) x) : null);
		
		registerType("material", CreativeIngredientMaterial.class, (x) -> x instanceof Material ? new CreativeIngredientMaterial((Material) x) : null);
		registerType("fuel", CreativeIngredientFuel.class, null);
		
		ConfigTypeConveration.registerSpecialType((x) -> CreativeIngredient.class.isAssignableFrom(x), new ConfigTypeConveration.SimpleConfigTypeConveration<CreativeIngredient>() {
			
			@Override
			public CreativeIngredient readElement(CreativeIngredient defaultValue, boolean loadDefault, JsonElement element) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
					try {
						return CreativeIngredient.read(JsonToNBT.getTagFromJson(element.getAsString()));
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
					}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(CreativeIngredient value, CreativeIngredient defaultValue, boolean saveDefault) {
				return new JsonPrimitive(value.write(new CompoundNBT()).toString());
			}
		});
	}
	
	public CreativeIngredient() {
		
	}
	
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putString("id", getId(this));
		writeExtra(nbt);
		return nbt;
	}
	
	protected abstract void writeExtra(CompoundNBT nbt);
	
	protected abstract void readExtra(CompoundNBT nbt);
	
	public abstract boolean is(ItemStack stack);
	
	public abstract boolean is(CreativeIngredient info);
	
	public abstract ItemStack getExample();
	
	@Override
	public boolean equals(Object object) {
		return object instanceof CreativeIngredient && equals((CreativeIngredient) object);
	}
	
	public abstract boolean equals(CreativeIngredient object);
	
}
