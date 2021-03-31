package com.creativemd.creativecore.common.config.converation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.holder.ConfigHolderDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.premade.NamedList;
import com.creativemd.creativecore.common.config.premade.Permission;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ConfigTypeConveration<T> {
    
    private static HashMap<Class, Supplier> typeCreators = new HashMap<>();
    private static HashMap<Class, ConfigTypeConveration> types = new HashMap<>();
    private static PairList<Predicate<Class>, ConfigTypeConveration> specialTypes = new PairList<>();
    
    public static final ICreativeConfigHolder fakeParent = new ICreativeConfigHolder() {
        
        @Override
        public ConfigSynchronization synchronization() {
            return ConfigSynchronization.UNIVERSAL;
        }
        
        @Override
        public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side) {
            return null;
        }
        
        @Override
        public void restoreDefault(Side side, boolean ignoreRestart) {}
        
        @Override
        public String[] path() {
            return new String[0];
        }
        
        @Override
        public ICreativeConfigHolder parent() {
            return null;
        }
        
        @Override
        public Collection<String> names() {
            return null;
        }
        
        @Override
        public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {}
        
        @Override
        public boolean isEmptyWithoutForce(Side side) {
            return false;
        }
        
        @Override
        public boolean isEmpty(Side side) {
            return false;
        }
        
        @Override
        public boolean isDefault(Side side) {
            return false;
        }
        
        @Override
        public ConfigKey getField(String key) {
            return null;
        }
        
        @Override
        public Object get(String key) {
            return null;
        }
        
        @Override
        public Collection<? extends ConfigKey> fields() {
            return null;
        }
    };
    public static ConfigTypeConveration<ConfigHolderObject> holderConveration;
    
    public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Supplier<T> type) {
        typeCreators.put(clazz, type);
    }
    
    public static <T, U extends T> ConfigTypeConveration<T> registerType(Class<U> clazz, ConfigTypeConveration<T> type) {
        types.put(clazz, type);
        return type;
    }
    
    public static void registerSpecialType(Predicate<Class> predicate, ConfigTypeConveration type) {
        specialTypes.add(predicate, type);
    }
    
    public static boolean has(Class typeClass) {
        if (types.containsKey(typeClass))
            return true;
        
        if (typeClass.isAnnotationPresent(CreativeConfig.class))
            return false;
        
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
    
    public static ConfigTypeConveration getUnsafe(Class typeClass) {
        ConfigTypeConveration converation = types.get(typeClass);
        if (converation != null)
            return converation;
        for (int i = 0; i < specialTypes.size(); i++)
            if (specialTypes.get(i).key.test(typeClass))
                return specialTypes.get(i).value;
        return null;
    }
    
    public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        return get(typeClass).readElement(defaultValue, loadDefault, ignoreRestart, element, side, key);
    }
    
    public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        return get(typeClass).writeElement(value, defaultValue, saveDefault, ignoreRestart, side, key);
    }
    
    public static Object createObject(Class clazz) {
        Supplier supplier = typeCreators.get(clazz);
        if (supplier != null)
            return supplier.get();
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {}
        return null;
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
        
        registerTypeCreator(boolean.class, () -> false);
        registerTypeCreator(Boolean.class, () -> Boolean.FALSE);
        
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
                GuiControl control = parent.get("data");
                if (control instanceof GuiSteppedSlider) {
                    GuiSteppedSlider button = (GuiSteppedSlider) control;
                    button.setValue(value.intValue());
                } else if (control instanceof GuiAnalogeSlider) {
                    GuiAnalogeSlider button = (GuiAnalogeSlider) control;
                    button.setValue(value.doubleValue());
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
                GuiControl control = parent.get("data");
                String text;
                if (control instanceof GuiSteppedSlider) {
                    GuiSteppedSlider button = (GuiSteppedSlider) control;
                    text = "" + ((int) button.value);
                } else if (control instanceof GuiAnalogeSlider) {
                    GuiAnalogeSlider button = (GuiAnalogeSlider) control;
                    text = "" + button.value;
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
        
        registerTypeCreator(byte.class, () -> (byte) 0);
        registerTypeCreator(Byte.class, () -> new Byte((byte) 0));
        registerTypeCreator(short.class, () -> (short) 0);
        registerTypeCreator(Short.class, () -> new Short((short) 0));
        registerTypeCreator(int.class, () -> 0);
        registerTypeCreator(Integer.class, () -> new Integer(0));
        registerTypeCreator(long.class, () -> (long) 0);
        registerTypeCreator(Long.class, () -> new Long(0));
        registerTypeCreator(float.class, () -> 0F);
        registerTypeCreator(Float.class, () -> new Float(0));
        registerTypeCreator(double.class, () -> 0D);
        registerTypeCreator(Double.class, () -> new Double(0));
        
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
        registerTypeCreator(String.class, () -> "");
        
        registerType(NamedList.class, new ConfigTypeNamedList());
        registerType(Permission.class, new ConfigTypePermission());
        
        holderConveration = registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
            
            @Override
            public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
                if (element.isJsonObject())
                    defaultValue.load(loadDefault, ignoreRestart, (JsonObject) element, side);
                else
                    defaultValue.restoreDefault(side, ignoreRestart);
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
                return value.save(saveDefault, ignoreRestart, side);
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
            public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
                if (element.isJsonObject())
                    defaultValue.load(loadDefault, ignoreRestart, (JsonObject) element, side);
                else
                    defaultValue.restoreDefault(side, ignoreRestart);
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
                return value.save(saveDefault, ignoreRestart, side);
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
        }, new ConfigTypeArray());
        
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
                Object[] possibleValues = clazz.getEnumConstants();
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
                return (Enum) clazz.getEnumConstants()[box.index];
            }
            
            @Override
            public Enum set(ConfigKeyField key, Enum value) {
                return value;
            }
        });
        
        registerSpecialType((x) -> List.class.isAssignableFrom(x) || x == ArrayList.class, new ConfigTypeList());
    }
    
    public abstract T readElement(T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key);
    
    public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key);
    
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
    
    public boolean areEqual(T one, T two, @Nullable ConfigKeyField key) {
        return one.equals(two);
    }
    
    public static Object parseObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
        if (has(object.getClass()))
            return object;
        return new ConfigHolderObject(parent, synchronization, key, object);
    }
    
    public static abstract class SimpleConfigTypeConveration<T> extends ConfigTypeConveration<T> {
        
        @Override
        public T readElement(T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
            return readElement(defaultValue, loadDefault, element);
        }
        
        public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element);
        
        @Override
        public JsonElement writeElement(T value, T defaultValue, boolean ignoreRestart, boolean saveDefault, Side side, @Nullable ConfigKeyField key) {
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
