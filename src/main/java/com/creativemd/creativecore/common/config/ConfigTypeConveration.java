package com.creativemd.creativecore.common.config;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.holder.ConfigHolderDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject.ConfigKeyField;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.math.MathHelper;
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
	
	public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
		return get(typeClass).readElement(defaultValue, loadDefault, element, side, key);
	}
	
	public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
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
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
				parent.addControl(new GuiStateButton("data", 0, 0, 0, recommendedWidth, ChatFormatting.RED + "false", ChatFormatting.GREEN + "true"));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Boolean value, GuiParent parent) {
				GuiStateButton button = (GuiStateButton) parent.get("data");
				button.setState(value ? 1 : 0);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected Boolean saveValue(GuiParent parent, Class clazz) {
				GuiStateButton button = (GuiStateButton) parent.get("data");
				return button.getState() == 1;
			}
			
			@Override
			public Boolean set(ConfigKeyField key, Boolean value) {
				return value;
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
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
				boolean decimal = isDecimal(clazz);
				if (key != null) {
					if (decimal) {
						CreativeConfig.DecimalRange decRange = key.field.getAnnotation(CreativeConfig.DecimalRange.class);
						if (decRange != null && decRange.slider()) {
							parent.addControl(new GuiAnalogeSlider("data", 0, 0, recommendedWidth, 14, decRange.min(), decRange.min(), decRange.max()));
							return;
						}
					} else {
						CreativeConfig.IntRange intRange = key.field.getAnnotation(CreativeConfig.IntRange.class);
						if (intRange != null && intRange.slider()) {
							parent.addControl(new GuiSteppedSlider("data", 0, 0, recommendedWidth, 14, intRange.min(), intRange.min(), intRange.max()));
							return;
						}
					}
				}
				
				GuiTextfield textfield = new GuiTextfield("data", "", 0, 0, recommendedWidth, 14);
				if (decimal)
					textfield.setFloatOnly();
				else
					textfield.setNumbersIncludingNegativeOnly();
				parent.addControl(textfield);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Number value, GuiParent parent) {
				GuiControl control = (GuiControl) parent.get("data");
				if (control instanceof GuiAnalogeSlider) {
					GuiAnalogeSlider button = (GuiAnalogeSlider) control;
					button.setValue(value.doubleValue());
				} else if (control instanceof GuiSteppedSlider) {
					GuiSteppedSlider button = (GuiSteppedSlider) control;
					button.setValue(value.intValue());
				} else {
					GuiTextfield button = (GuiTextfield) control;
					button.text = value.toString();
					button.setCursorPositionZero();
				}
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
			
			public Number parseNumber(Class clazz, String text) {
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
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected Number saveValue(GuiParent parent, Class clazz) {
				boolean decimal = isDecimal(clazz);
				GuiControl control = (GuiControl) parent.get("data");
				String text;
				if (control instanceof GuiAnalogeSlider) {
					GuiAnalogeSlider button = (GuiAnalogeSlider) control;
					text = "" + button.value;
				} else if (control instanceof GuiSteppedSlider) {
					GuiSteppedSlider button = (GuiSteppedSlider) control;
					text = "" + ((int) button.value);
				} else {
					GuiTextfield button = (GuiTextfield) control;
					text = button.text;
				}
				return parseNumber(clazz, text);
			}
			
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
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
				parent.addControl(new GuiTextfield("data", "", 0, 0, recommendedWidth, 14));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(String value, GuiParent parent) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				button.text = value;
				button.setCursorPositionZero();
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected String saveValue(GuiParent parent, Class clazz) {
				GuiTextfield button = (GuiTextfield) parent.get("data");
				return button.text;
			}
			
			@Override
			public String set(ConfigKeyField key, String value) {
				return value;
			}
		});
		
		registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
			
			@Override
			public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
				return value.save(saveDefault, side);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(ConfigHolderObject value, GuiParent parent, @Nullable ConfigKeyField key) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected ConfigHolderObject saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
				return null;
			}
			
			@Override
			public ConfigHolderObject set(ConfigKeyField key, ConfigHolderObject value) {
				return null;
			}
		});
		
		registerType(ConfigHolderDynamic.class, new ConfigTypeConveration<ConfigHolderDynamic>() {
			
			@Override
			public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
				if (element.isJsonObject())
					defaultValue.load(loadDefault, (JsonObject) element, side);
				else
					defaultValue.restoreDefault(side);
				return defaultValue;
			}
			
			@Override
			public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
				return value.save(saveDefault, side);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(ConfigHolderDynamic value, GuiParent parent, @Nullable ConfigKeyField key) {
				
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected ConfigHolderDynamic saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
				return null;
			}
			
			@Override
			public ConfigHolderDynamic set(ConfigKeyField key, ConfigHolderDynamic value) {
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
			public Object readElement(Object defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
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
			public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
				int length = Array.getLength(value);
				JsonArray array = new JsonArray();
				for (int i = 0; i < length; i++)
					array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, side, null));
				return array;
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
				parent.height = 160;
				GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.width - 10, 150, false, new ArrayList<>());
				parent.addControl(listBox);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Object value, GuiParent parent, @Nullable ConfigKeyField key) {
				GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
				if (!box.isEmpty())
					box.clear();
				
				Class clazz = value.getClass().getComponentType();
				ConfigTypeConveration converation = get(clazz);
				
				int length = Array.getLength(value);
				List<GuiConfigSubControl> controls = new ArrayList<>(length);
				for (int i = 0; i < length; i++) {
					Object entry = Array.get(value, i);
					GuiConfigSubControl control = new GuiConfigSubControl("" + i, 2, 0, parent.width, 14);
					converation.createControls(control, null, clazz, Math.min(100, control.width));
					converation.loadValue(entry, control, null);
					controls.add(control);
				}
				
				box.addAll(controls);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected Object saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
				Class subClass = clazz.getComponentType();
				ConfigTypeConveration converation = get(subClass);
				
				GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
				Object value = Array.newInstance(subClass, box.size());
				for (int i = 0; i < box.size(); i++)
					Array.set(value, i, converation.save(box.get(i), subClass, null));
				return value;
			}
			
			@Override
			public Object set(ConfigKeyField key, Object value) {
				return value;
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
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
				Object[] possibleValues = clazz.getDeclaringClass().getEnumConstants();
				List<String> lines = new ArrayList<>(possibleValues.length);
				for (int i = 0; i < possibleValues.length; i++)
					lines.add(((Enum) possibleValues[i]).name());
				parent.addControl(new GuiComboBox("data", 0, 0, recommendedWidth, lines));
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(Enum value, GuiParent parent) {
				GuiComboBox box = (GuiComboBox) parent.get("data");
				box.select(value.ordinal());
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected Enum saveValue(GuiParent parent, Class clazz) {
				GuiComboBox box = (GuiComboBox) parent.get("data");
				return (Enum) clazz.getDeclaringClass().getEnumConstants()[box.index];
			}
			
			@Override
			public Enum set(ConfigKeyField key, Enum value) {
				return value;
			}
		});
		
		registerSpecialType((x) -> List.class.isAssignableFrom(x), new ConfigTypeConveration<List>() {
			
			@Override
			public List readElement(List defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
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
			public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
				JsonArray array = new JsonArray();
				Class clazz = getListType(key);
				for (int i = 0; i < value.size(); i++)
					array.add(write(clazz, value.get(i), null, saveDefault, side, null));
				return array;
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
				parent.height = 160;
				GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.width - 10, 130, true, new ArrayList<>());
				parent.addControl(listBox);
				
				Class subClass = getListType(key);
				ConfigTypeConveration converation = get(subClass);
				
				int parentWidth = parent.width;
				
				parent.addControl(new GuiButton("add", 0, 140) {
					
					@Override
					public void onClicked(int x, int y, int button) {
						GuiConfigSubControl control = new GuiConfigSubControl("" + 0, 2, 0, parentWidth, 14);
						converation.createControls(control, null, subClass, Math.min(100, control.width));
						listBox.add(control);
					}
				});
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public void loadValue(List value, GuiParent parent, @Nullable ConfigKeyField key) {
				GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
				if (!box.isEmpty())
					box.clear();
				
				Class clazz = getListType(key);
				ConfigTypeConveration converation = get(clazz);
				
				List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
				for (int i = 0; i < value.size(); i++) {
					Object entry = value.get(i);
					GuiConfigSubControl control = new GuiConfigSubControl("" + i, 2, 0, parent.width, 14);
					converation.createControls(control, null, clazz, Math.min(100, control.width));
					converation.loadValue(entry, control, null);
					controls.add(control);
				}
				
				box.addAll(controls);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			protected List saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
				Class subClass = getListType(key);
				ConfigTypeConveration converation = get(subClass);
				
				GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
				List value = new ArrayList(box.size());
				for (int i = 0; i < box.size(); i++)
					value.add(converation.save(box.get(i), subClass, null));
				return value;
			}
			
			@Override
			public List set(ConfigKeyField key, List value) {
				return value;
			}
			
			public Class getListType(ConfigKeyField key) {
				ParameterizedType type = (ParameterizedType) key.field.getGenericType();
				return (Class) type.getActualTypeArguments()[0];
			}
		});
	}
	
	public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key);
	
	public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key);
	
	@SideOnly(Side.CLIENT)
	public abstract void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth);
	
	@SideOnly(Side.CLIENT)
	public abstract void loadValue(T value, GuiParent parent, @Nullable ConfigKeyField key);
	
	@SideOnly(Side.CLIENT)
	protected abstract T saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key);
	
	public abstract T set(ConfigKeyField key, T value);
	
	@SideOnly(Side.CLIENT)
	public T save(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
		T value = saveValue(parent, clazz, key);
		if (value != null && key != null)
			return set(key, value);
		return value;
	}
	
	public boolean areEqual(T one, T two) {
		return one.equals(two);
	}
	
	public static Object parseObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
		if (has(object.getClass()))
			return object;
		return new ConfigHolderObject(parent, synchronization, key, object);
	}
	
	public static abstract class SimpleConfigTypeConveration<T> extends ConfigTypeConveration<T> {
		
		@Override
		public T readElement(T defaultValue, boolean loadDefault, JsonElement element, Side side, @Nullable ConfigKeyField key) {
			return readElement(defaultValue, loadDefault, element);
		}
		
		public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element);
		
		@Override
		public JsonElement writeElement(T value, T defaultValue, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
			return writeElement(value, defaultValue, saveDefault);
		}
		
		public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault);
		
		@Override
		@SideOnly(Side.CLIENT)
		public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
			createControls(parent, clazz, recommendedWidth);
		}
		
		@SideOnly(Side.CLIENT)
		public abstract void createControls(GuiParent parent, Class clazz, int recommendedWidth);
		
		@Override
		@SideOnly(Side.CLIENT)
		public void loadValue(T value, GuiParent parent, @Nullable ConfigKeyField key) {
			loadValue(value, parent);
		}
		
		@SideOnly(Side.CLIENT)
		public abstract void loadValue(T value, GuiParent parent);
		
		@Override
		@SideOnly(Side.CLIENT)
		protected T saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
			return saveValue(parent, clazz);
		}
		
		@SideOnly(Side.CLIENT)
		protected abstract T saveValue(GuiParent parent, Class clazz);
		
	}
	
}
