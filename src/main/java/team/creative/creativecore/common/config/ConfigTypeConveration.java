package team.creative.creativecore.common.config;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.holder.ConfigHolderDynamic;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.util.type.PairList;

public abstract class ConfigTypeConveration<T> {
	
	private static HashMap<Class, ConfigTypeConveration> types = new HashMap<>();
	private static PairList<Predicate<Class>, ConfigTypeConveration> specialTypes = new PairList<>();
	
	public static <T, U extends T> void registerType(Class<U> clazz, ConfigTypeConveration<T> type) {
		types.put(clazz, type);
	}
	
	public static void registerSpecialType(Predicate<Class> predicate, ConfigTypeConveration type) {
		specialTypes.add(predicate, type);
	}
	
	public static boolean has(Class typeClass) {
		if (types.containsKey(typeClass))
			return true;
		
		for (int i = 0; i < specialTypes.size(); i++)
			if (specialTypes.get(i).key.test(typeClass))
				return true;
			
		return false;
	}
	
	public static ConfigTypeConveration get(Class typeClass) {
		ConfigTypeConveration converation = types.get(typeClass);
		if (converation != null)
			return converation;
		for (int i = 0; i < specialTypes.size(); i++)
			if (specialTypes.get(i).key.test(typeClass))
				return specialTypes.get(i).value;
		throw new RuntimeException("Could not find converation for " + typeClass.getName());
	}
	
	public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
		return get(typeClass).readElement(defaultValue, loadDefault, element, side, key);
	}
	
	public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
		return get(typeClass).writeElement(value, defaultValue, saveDefault, side, key);
	}
	
	static {
		ConfigTypeConveration<Boolean> booleanType = new SimpleConfigTypeConveration<Boolean>() {
			
			@Override
			public Boolean readElement(Boolean defaultValue, boolean loadDefault, JsonElement element) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isBoolean())
					return element.getAsBoolean();
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Boolean value, Boolean defaultValue, boolean saveDefault) {
				return new JsonPrimitive(value);
			}
		};
		registerType(boolean.class, booleanType);
		registerType(Boolean.class, booleanType);
		
		ConfigTypeConveration<Number> numberType = new SimpleConfigTypeConveration<Number>() {
			
			@Override
			public Number readElement(Number defaultValue, boolean loadDefault, JsonElement element) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
					Class clazz = defaultValue.getClass();
					if (clazz == Float.class || clazz == float.class)
						return element.getAsFloat();
					else if (clazz == Double.class || clazz == double.class)
						return element.getAsDouble();
					else if (clazz == Byte.class || clazz == byte.class)
						return element.getAsByte();
					else if (clazz == Short.class || clazz == short.class)
						return element.getAsShort();
					else if (clazz == Integer.class || clazz == int.class)
						return element.getAsInt();
					else if (clazz == Long.class || clazz == long.class)
						return element.getAsLong();
					return element.getAsNumber();
				}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Number value, Number defaultValue, boolean saveDefault) {
				return new JsonPrimitive(value);
			}
			
			public boolean isDecimal(Class clazz) {
				return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
			}
			
			public Number parseDecimal(Class clazz, double decimal) {
				if (clazz == Float.class || clazz == float.class)
					return (float) decimal;
				return decimal;
			}
			
			public Number parseInt(Class clazz, int number) {
				if (clazz == Byte.class || clazz == byte.class)
					return (byte) number;
				if (clazz == Short.class || clazz == short.class)
					return (short) number;
				if (clazz == Long.class || clazz == long.class)
					return (long) number;
				return number;
			}
			
			/*public Number parseNumber(Class clazz, String text) {
				if (clazz == Float.class || clazz == float.class)
					try {
						return Float.parseFloat(text);
					} catch (NumberFormatException e) {
						return (float) 0;
					}
				else if (clazz == Double.class || clazz == double.class)
					try {
						return Double.parseDouble(text);
					} catch (NumberFormatException e) {
						return (double) 0;
					}
				else if (clazz == Byte.class || clazz == byte.class)
					try {
						return Byte.parseByte(text);
					} catch (NumberFormatException e) {
						return (byte) 0;
					}
				else if (clazz == Short.class || clazz == short.class)
					try {
						return Short.parseShort(text);
					} catch (NumberFormatException e) {
						return (short) 0;
					}
				else if (clazz == Integer.class || clazz == int.class)
					try {
						return Integer.parseInt(text);
					} catch (NumberFormatException e) {
						return (int) 0;
					}
				else if (clazz == Long.class || clazz == long.class)
					try {
						return Long.parseLong(text);
					} catch (NumberFormatException e) {
						return (long) 0;
					}
				else
					return 0;
				
			}*/
			
			@Override
			public Number set(ConfigKeyField key, Number value) {
				Class clazz = key.getType();
				boolean decimal = isDecimal(clazz);
				if (key != null) {
					if (decimal) {
						CreativeConfig.DecimalRange decRange = key.field.getAnnotation(CreativeConfig.DecimalRange.class);
						if (decRange != null)
							return parseDecimal(clazz, MathHelper.clamp(value.doubleValue(), decRange.min(), decRange.max()));
					} else {
						CreativeConfig.IntRange intRange = key.field.getAnnotation(CreativeConfig.IntRange.class);
						if (intRange != null)
							return parseInt(clazz, MathHelper.clamp(value.intValue(), intRange.min(), intRange.max()));
					}
				}
				return value;
			}
		};
		registerType(byte.class, numberType);
		registerType(Byte.class, numberType);
		registerType(short.class, numberType);
		registerType(Short.class, numberType);
		registerType(int.class, numberType);
		registerType(Integer.class, numberType);
		registerType(long.class, numberType);
		registerType(Long.class, numberType);
		registerType(float.class, numberType);
		registerType(Float.class, numberType);
		registerType(double.class, numberType);
		registerType(Double.class, numberType);
		
		registerType(String.class, new SimpleConfigTypeConveration<String>() {
			
			@Override
			public String readElement(String defaultValue, boolean loadDefault, JsonElement element) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
					return element.getAsString();
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(String value, String defaultValue, boolean saveDefault) {
				return new JsonPrimitive(value);
			}
		});
		
		registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
			
			@Override
			public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
				return value.save(saveDefault, side);
			}
		});
		
		registerType(ConfigHolderDynamic.class, new ConfigTypeConveration<ConfigHolderDynamic>() {
			
			@Override
			public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
				return value.save(saveDefault, side);
			}
		});
		
		registerSpecialType((x) -> {
			if (x.isArray()) {
				if (has(x.getComponentType()))
					return true;
				throw new RuntimeException("Array with holders are not permitted");
			}
			return false;
		}, new ConfigTypeConveration() {
			
			@Override
			public Object readElement(Object defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
				if (element.isJsonArray()) {
					JsonArray array = (JsonArray) element;
					int size = Math.min(array.size(), Array.getLength(defaultValue));
					Object object = Array.newInstance(defaultValue.getClass().getComponentType(), size);
					for (int i = 0; i < size; i++)
						Array.set(object, i, read(defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, array.get(i), side, null));
					return object;
				}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
				int length = Array.getLength(value);
				JsonArray array = new JsonArray();
				for (int i = 0; i < length; i++)
					array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, side, null));
				return array;
			}
			
			@Override
			public boolean areEqual(Object one, Object two) {
				int lengthOne = Array.getLength(one);
				int lengthTwo = Array.getLength(two);
				
				if (lengthOne != lengthTwo)
					return false;
				
				for (int i = 0; i < lengthOne; i++) {
					Object entryOne = Array.get(one, i);
					Object entryTwo = Array.get(two, i);
					
					if (entryOne.getClass().isArray()) {
						if (!entryTwo.getClass().isArray())
							return false;
						
						if (!areEqual(entryOne, entryTwo))
							return false;
					}
					
					if (!entryOne.equals(entryTwo))
						return false;
				}
				
				return true;
			}
			
		});
		
		registerSpecialType((x) -> x.isEnum(), new SimpleConfigTypeConveration<Enum>() {
			
			@Override
			public Enum readElement(Enum defaultValue, boolean loadDefault, JsonElement element) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
					return Enum.valueOf(defaultValue.getDeclaringClass(), element.getAsString());
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Enum value, Enum defaultValue, boolean saveDefault) {
				return new JsonPrimitive(value.name());
			}
		});
		
		registerSpecialType((x) -> x == List.class || x == ArrayList.class, new ConfigTypeConveration<List>() {
			
			@Override
			public List readElement(List defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
				if (element.isJsonArray()) {
					JsonArray array = (JsonArray) element;
					Class clazz = getListType(key);
					List list = new ArrayList<>(array.size());
					for (int i = 0; i < array.size(); i++)
						list.add(read(clazz, null, loadDefault, array.get(i), side, null));
					return list;
				}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
				JsonArray array = new JsonArray();
				Class clazz = getListType(key);
				for (int i = 0; i < value.size(); i++)
					array.add(write(clazz, value.get(i), null, saveDefault, side, null));
				return array;
			}
			
			public Class getListType(ConfigKeyField key) {
				ParameterizedType type = (ParameterizedType) key.field.getGenericType();
				return (Class) type.getActualTypeArguments()[0];
			}
		});
	}
	
	public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key);
	
	public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key);
	
	public boolean areEqual(T one, T two) {
		return one.equals(two);
	}
	
	public T set(ConfigKeyField key, T value) {
		return value;
	}
	
	public static Object parseObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
		if (has(object.getClass()))
			return object;
		return new ConfigHolderObject(parent, synchronization, key, object);
	}
	
	public static abstract class SimpleConfigTypeConveration<T> extends ConfigTypeConveration<T> {
		
		@Override
		public T readElement(T defaultValue, boolean loadDefault, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
			return readElement(defaultValue, loadDefault, element);
		}
		
		public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element);
		
		@Override
		public JsonElement writeElement(T value, T defaultValue, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
			return writeElement(value, defaultValue, saveDefault);
		}
		
		public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault);
		
	}
	
}
