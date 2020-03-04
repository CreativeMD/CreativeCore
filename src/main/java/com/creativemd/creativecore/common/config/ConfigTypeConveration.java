package com.creativemd.creativecore.common.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.NotImplementedException;

import com.creativemd.creativecore.common.config.gui.GuiConfigControl;
import com.creativemd.creativecore.common.config.holder.ConfigHolderDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	
	public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, JsonElement element, Side side) {
		return get(typeClass).readElement(defaultValue, loadDefault, element, side);
	}
	
	public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, Side side) {
		return get(typeClass).writeElement(value, defaultValue, saveDefault, side);
	}
	
	static {
		ConfigTypeConveration<Boolean> booleanType = new ConfigTypeConveration<Boolean>() {
			
			@Override
			public Boolean readElement(Boolean defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isBoolean())
					return element.getAsBoolean();
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Boolean value, Boolean defaultValue, boolean saveDefault, Side side) {
				return new JsonPrimitive(value);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				parent.addControl(new GuiStateButton("data", 0, 0, 0, 100, ChatFormatting.RED + "false", ChatFormatting.GREEN + "true"));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Boolean value, GuiConfigControl parent) {
				GuiStateButton button = (GuiStateButton) parent.get("data");
				button.setState(value ? 1 : 0);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Boolean saveValue(GuiConfigControl parent) {
				GuiStateButton button = (GuiStateButton) parent.get("data");
				return button.getState() == 1;
			}
		};
		registerType(boolean.class, booleanType);
		registerType(Boolean.class, booleanType);
		
		ConfigTypeConveration<Number> numberType = new ConfigTypeConveration<Number>() {
			
			@Override
			public Number readElement(Number defaultValue, boolean loadDefault, JsonElement element, Side side) {
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
			public JsonElement writeElement(Number value, Number defaultValue, boolean saveDefault, Side side) {
				return new JsonPrimitive(value);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				GuiTextfield textfield = new GuiTextfield("data", "", 0, 0, 100, 14);
				if (parent.field.getType() == Float.class || parent.field.getType() == float.class || parent.field.getType() == Double.class || parent.field.getType() == double.class)
					textfield.setFloatOnly();
				else
					textfield.setNumbersIncludingNegativeOnly();
				parent.addControl(textfield);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Number value, GuiConfigControl parent) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				button.text = value.toString();
				button.setCursorPositionZero();
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Number saveValue(GuiConfigControl parent) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				Number value;
				Class clazz = parent.field.getType();
				try {
					if (clazz == Float.class || clazz == float.class)
						value = button.parseFloat();
					else if (clazz == Double.class || clazz == double.class)
						value = Double.parseDouble(button.text);
					else if (clazz == Byte.class || clazz == byte.class)
						value = Byte.parseByte(button.text);
					else if (clazz == Short.class || clazz == short.class)
						value = Short.parseShort(button.text);
					else if (clazz == Integer.class || clazz == int.class)
						value = button.parseInteger();
					else if (clazz == Long.class || clazz == long.class)
						value = Long.parseLong(button.text);
					else
						value = 0;
				} catch (NumberFormatException e) {
					value = 0;
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
		
		registerType(String.class, new ConfigTypeConveration<String>() {
			
			@Override
			public String readElement(String defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
					return element.getAsString();
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(String value, String defaultValue, boolean saveDefault, Side side) {
				return new JsonPrimitive(value);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				parent.addControl(new GuiTextfield("data", "", 0, 0, 100, 14));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(String value, GuiConfigControl parent) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				button.text = value;
				button.setCursorPositionZero();
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public String saveValue(GuiConfigControl parent) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				return button.text;
			}
		});
		
		registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
			
			@Override
			public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, Side side) {
				return value.save(saveDefault, side);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(ConfigHolderObject value, GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public ConfigHolderObject saveValue(GuiConfigControl parent) {
				
				return null;
			}
		});
		
		registerType(ConfigHolderDynamic.class, new ConfigTypeConveration<ConfigHolderDynamic>() {
			
			@Override
			public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, Side side) {
				return value.save(saveDefault, side);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(ConfigHolderDynamic value, GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public ConfigHolderDynamic saveValue(GuiConfigControl parent) {
				
				return null;
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
			public Object readElement(Object defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonArray()) {
					JsonArray array = (JsonArray) element;
					Object object = Array.newInstance(defaultValue.getClass().getComponentType(), array.size());
					for (int i = 0; i < array.size(); i++)
						Array.set(object, i, read(defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, array.get(i), side));
					return object;
				}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, Side side) {
				int length = Array.getLength(value);
				JsonArray array = new JsonArray();
				for (int i = 0; i < length; i++)
					array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, side));
				return array;
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				throw new NotImplementedException("");
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Object value, GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Object saveValue(GuiConfigControl parent) {
				
				return null;
			}
		});
		
		registerSpecialType((x) -> x.isEnum(), new ConfigTypeConveration<Enum>() {
			
			@Override
			public Enum readElement(Enum defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
					return Enum.valueOf(defaultValue.getDeclaringClass(), element.getAsString());
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(Enum value, Enum defaultValue, boolean saveDefault, Side side) {
				return new JsonPrimitive(value.name());
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				Object[] possibleValues = parent.field.getType().getDeclaringClass().getEnumConstants();
				List<String> lines = new ArrayList<>(possibleValues.length);
				for (int i = 0; i < possibleValues.length; i++)
					lines.add(((Enum) possibleValues[i]).name());
				parent.addControl(new GuiComboBox("data", 0, 0, 100, lines));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Enum value, GuiConfigControl parent) {
				GuiComboBox box = (GuiComboBox) parent.get("data");
				box.select(value.ordinal());
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Enum saveValue(GuiConfigControl parent) {
				GuiComboBox box = (GuiComboBox) parent.get("data");
				return (Enum) parent.field.getType().getDeclaringClass().getEnumConstants()[box.index];
			}
		});
		
		registerSpecialType((x) -> {
			if (x == ArrayList.class || x == List.class) {
				if (has(x.getComponentType()))
					return true;
				throw new RuntimeException("List with holders are not permitted");
			}
			return false;
		}, new ConfigTypeConveration<List>() {
			
			@Override
			public List readElement(List defaultValue, boolean loadDefault, JsonElement element, Side side) {
				if (element.isJsonArray()) {
					JsonArray array = (JsonArray) element;
					List list = new ArrayList<>(array.size());
					for (int i = 0; i < array.size(); i++)
						list.set(i, read(defaultValue.getClass().getComponentType(), null, loadDefault, array.get(i), side));
					return list;
				}
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, Side side) {
				JsonArray array = new JsonArray();
				for (int i = 0; i < value.size(); i++)
					array.add(write(value.getClass().getComponentType(), value.get(i), null, saveDefault, side));
				return array;
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiConfigControl parent) {
				throw new NotImplementedException("");
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(List value, GuiConfigControl parent) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public List saveValue(GuiConfigControl parent) {
				
				return null;
			}
		});
	}
	
	public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element, Side side);
	
	public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault, Side side);
	
	@SideOnly(Side.CLIENT)
	public abstract void createControls(GuiConfigControl parent);
	
	@SideOnly(Side.CLIENT)
	public abstract void loadValue(T value, GuiConfigControl parent);
	
	@SideOnly(Side.CLIENT)
	public abstract T saveValue(GuiConfigControl parent);
	
	public static Object parseObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
		if (has(object.getClass()))
			return object;
		return new ConfigHolderObject(parent, synchronization, key, object);
	}
	
}
