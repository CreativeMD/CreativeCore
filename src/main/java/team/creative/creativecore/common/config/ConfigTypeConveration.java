package team.creative.creativecore.common.config;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.holder.ConfigHolderDynamic;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.premade.SoundConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiComboBox;
import team.creative.creativecore.common.gui.controls.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.GuiSlider;
import team.creative.creativecore.common.gui.controls.GuiStateButton;
import team.creative.creativecore.common.gui.controls.GuiSteppedSlider;
import team.creative.creativecore.common.gui.controls.GuiTextfield;
import team.creative.creativecore.common.gui.controls.layout.GuiHBox;
import team.creative.creativecore.common.util.text.TextListBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.PairList;

public abstract class ConfigTypeConveration<T> {
    
    private static HashMap<Class, ConfigTypeConveration> types = new HashMap<>();
    private static PairList<Predicate<Class>, ConfigTypeConveration> specialTypes = new PairList<>();
    
    private static final ICreativeConfigHolder fakeParent = new ICreativeConfigHolder() {
        
        @Override
        public ConfigSynchronization synchronization() {
            return ConfigSynchronization.UNIVERSAL;
        }
        
        @Override
        public JsonObject save(boolean saveDefault, boolean ignoreRestart, Dist side) {
            return null;
        }
        
        @Override
        public void restoreDefault(Dist side, boolean ignoreRestart) {}
        
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
        public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Dist side) {}
        
        @Override
        public boolean isEmptyWithoutForce(Dist side) {
            return false;
        }
        
        @Override
        public boolean isEmpty(Dist side) {
            return false;
        }
        
        @Override
        public boolean isDefault(Dist side) {
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
    private static ConfigTypeConveration<ConfigHolderObject> holderConveration;
    
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
    
    public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
        return get(typeClass).readElement(defaultValue, loadDefault, ignoreRestart, element, side, key);
    }
    
    public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
        return get(typeClass).writeElement(value, defaultValue, saveDefault, ignoreRestart, side, key);
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
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
                parent.add(new GuiStateButton("data", 0, 0, 0, TextFormatting.RED + "false", TextFormatting.GREEN + "true"));
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(Boolean value, GuiParent parent) {
                GuiStateButton button = (GuiStateButton) parent.get("data");
                button.setState(value ? 1 : 0);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected Boolean saveValue(GuiParent parent, Class clazz) {
                GuiStateButton button = (GuiStateButton) parent.get("data");
                return button.getState() == 1;
            }
            
            @Override
            public Boolean set(ConfigKeyField key, Boolean value) {
                return value;
            }
            
            @Override
            public Boolean createPrimitiveDefault(Class clazz) {
                return Boolean.FALSE;
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
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
                boolean decimal = isDecimal(clazz);
                if (key != null) {
                    if (decimal) {
                        CreativeConfig.DecimalRange decRange = key.field.getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null && decRange.slider()) {
                            parent.add(new GuiSlider("data", 0, 0, recommendedWidth, 14, decRange.min(), decRange.min(), decRange.max()));
                            return;
                        }
                    } else {
                        CreativeConfig.IntRange intRange = key.field.getAnnotation(CreativeConfig.IntRange.class);
                        if (intRange != null && intRange.slider()) {
                            parent.add(new GuiSteppedSlider("data", 0, 0, recommendedWidth, 14, intRange.min(), intRange.min(), intRange.max()));
                            return;
                        }
                    }
                }
                
                GuiTextfield textfield = new GuiTextfield("data", 0, 0, recommendedWidth, 14);
                if (decimal)
                    textfield.setFloatOnly();
                else
                    textfield.setNumbersIncludingNegativeOnly();
                parent.add(textfield);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(Number value, GuiParent parent) {
                GuiControl control = parent.get("data");
                if (control instanceof GuiSteppedSlider) {
                    GuiSteppedSlider button = (GuiSteppedSlider) control;
                    button.setValue(value.intValue());
                } else if (control instanceof GuiSlider) {
                    GuiSlider button = (GuiSlider) control;
                    button.setValue(value.doubleValue());
                } else
                    ((GuiTextfield) control).setText(value.toString());
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
            @OnlyIn(value = Dist.CLIENT)
            protected Number saveValue(GuiParent parent, Class clazz) {
                GuiControl control = parent.get("data");
                String text;
                if (control instanceof GuiSteppedSlider) {
                    GuiSteppedSlider button = (GuiSteppedSlider) control;
                    text = "" + ((int) button.value);
                } else if (control instanceof GuiSlider) {
                    GuiSlider button = (GuiSlider) control;
                    text = "" + button.value;
                } else
                    text = ((GuiTextfield) control).getText();
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
            
            @Override
            public Number createPrimitiveDefault(Class clazz) {
                if (clazz == Float.class || clazz == float.class)
                    return new Float(0);
                else if (clazz == Double.class || clazz == double.class)
                    return new Double(0);
                else if (clazz == Byte.class || clazz == byte.class)
                    return new Byte((byte) 0);
                else if (clazz == Short.class || clazz == short.class)
                    return new Short((short) 0);
                else if (clazz == Integer.class || clazz == int.class)
                    return new Integer(0);
                else if (clazz == Long.class || clazz == long.class)
                    return new Long(0);
                return 0;
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
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
                parent.add(new GuiTextfield("data", 0, 0, recommendedWidth, 14));
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(String value, GuiParent parent) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                button.setText(value);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected String saveValue(GuiParent parent, Class clazz) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                return button.getText();
            }
            
            @Override
            public String set(ConfigKeyField key, String value) {
                return value;
            }
            
            @Override
            public String createPrimitiveDefault(Class clazz) {
                return "";
            }
        });
        
        registerType(SoundConfig.class, new ConfigTypeConveration<SoundConfig>() {
            
            @Override
            public SoundConfig createPrimitiveDefault(Class clazz) {
                return new SoundConfig(new ResourceLocation("missing"));
            }
            
            @Override
            public SoundConfig readElement(SoundConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, ConfigKeyField key) {
                if (element.isJsonObject())
                    return new SoundConfig(new ResourceLocation(element.getAsJsonObject().get("sound").getAsString()), element.getAsJsonObject().get("volume").getAsFloat(), element
                            .getAsJsonObject().get("pitch").getAsFloat());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(SoundConfig value, SoundConfig defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, ConfigKeyField key) {
                JsonObject json = new JsonObject();
                json.addProperty("sound", value.event.toString());
                json.addProperty("volume", value.volume);
                json.addProperty("pitch", value.pitch);
                return json;
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKeyField key, Class clazz, int recommendedWidth) {
                parent.add(new GuiTextfield("search", 0, 0, recommendedWidth, 14));
                parent.add(new GuiComboBoxMapped<ResourceLocation>("sound", 0, 14, new TextMapBuilder<ResourceLocation>()
                        .addComponent(ForgeRegistries.SOUND_EVENTS.getKeys(), x -> new StringTextComponent(x.toString()))));
                GuiHBox hBox = new GuiHBox("vBox", 0, 30);
                hBox.add(new GuiLabel("volumeLabel", 0, 0).setTitle(new TranslationTextComponent("gui.volume")));
                hBox.add(new GuiSlider("volume", 0, 30, 40, 10, 1, 0, 1));
                hBox.add(new GuiLabel("pitchLabel", 0, 0).setTitle(new TranslationTextComponent("gui.pitch")));
                hBox.add(new GuiSlider("pitch", 30, 30, 40, 10, 1, 0.5, 2));
                parent.add(hBox);
                parent.setHeight(45);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(SoundConfig value, GuiParent parent, ConfigKeyField key) {
                GuiComboBoxMapped<ResourceLocation> box = (GuiComboBoxMapped<ResourceLocation>) parent.get("sound");
                GuiSlider volume = (GuiSlider) parent.get("volume");
                GuiSlider pitch = (GuiSlider) parent.get("pitch");
                
                box.select(value.event);
                volume.setValue(value.volume);
                pitch.setValue(value.pitch);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected SoundConfig saveValue(GuiParent parent, Class clazz, ConfigKeyField key) {
                GuiComboBoxMapped<ResourceLocation> box = (GuiComboBoxMapped<ResourceLocation>) parent.get("sound");
                GuiSlider volume = (GuiSlider) parent.get("volume");
                GuiSlider pitch = (GuiSlider) parent.get("pitch");
                
                return new SoundConfig(box.getSelected(), (float) volume.value, (float) pitch.value);
            }
            
            @Override
            public SoundConfig set(ConfigKeyField key, SoundConfig value) {
                return value;
            }
            
        });
        
        holderConveration = registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
            
            @Override
            public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
                if (element.isJsonObject())
                    defaultValue.load(loadDefault, ignoreRestart, (JsonObject) element, side);
                else
                    defaultValue.restoreDefault(side, ignoreRestart);
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
                return value.save(saveDefault, ignoreRestart, side);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
            
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(ConfigHolderObject value, GuiParent parent, @Nullable ConfigKeyField key) {
                
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected ConfigHolderObject saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
                return null;
            }
            
            @Override
            public ConfigHolderObject set(ConfigKeyField key, ConfigHolderObject value) {
                return null;
            }
            
            @Override
            public ConfigHolderObject createPrimitiveDefault(Class clazz) {
                return null;
            }
        });
            
        registerType(ConfigHolderDynamic.class, new ConfigTypeConveration<ConfigHolderDynamic>() {
            
            @Override
            public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
                if (element.isJsonObject())
                    defaultValue.load(loadDefault, ignoreRestart, (JsonObject) element, side);
                else
                    defaultValue.restoreDefault(side, ignoreRestart);
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
                return value.save(saveDefault, ignoreRestart, side);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
            
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(ConfigHolderDynamic value, GuiParent parent, @Nullable ConfigKeyField key) {
                
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected ConfigHolderDynamic saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
                return null;
            }
            
            @Override
            public ConfigHolderDynamic set(ConfigKeyField key, ConfigHolderDynamic value) {
                return null;
            }
            
            @Override
            public ConfigHolderDynamic createPrimitiveDefault(Class clazz) {
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
            public Object readElement(Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
                if (element.isJsonArray()) {
                    JsonArray array = (JsonArray) element;
                    int size = Math.min(array.size(), Array.getLength(defaultValue));
                    Object object = Array.newInstance(defaultValue.getClass().getComponentType(), size);
                    for (int i = 0; i < size; i++)
                        Array.set(object, i, read(defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, ignoreRestart, array.get(i), side, null));
                    return object;
                }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
                int length = Array.getLength(value);
                JsonArray array = new JsonArray();
                for (int i = 0; i < length; i++)
                    array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, ignoreRestart, side, null));
                return array;
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
                parent.setHeight(160);
                GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.getWidth() - 10, 150, false, new ArrayList<>());
                parent.add(listBox);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
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
                    GuiConfigSubControl control = new GuiConfigSubControl("" + i, 2, 0, parent.getWidth(), 14);
                    converation.createControls(control, null, clazz, Math.min(100, control.getWidth()));
                    converation.loadValue(entry, control, null);
                    controls.add(control);
                }
                
                box.addAllItems(controls);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
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
            
            @Override
            public Object createPrimitiveDefault(Class clazz) {
                return null;
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
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
                parent.add(new GuiComboBox("data", 0, 0, new TextListBuilder().add(clazz.getEnumConstants(), (x) -> ((Enum) x).name())));
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(Enum value, GuiParent parent) {
                GuiComboBox box = (GuiComboBox) parent.get("data");
                box.select(value.ordinal());
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected Enum saveValue(GuiParent parent, Class clazz) {
                GuiComboBox box = (GuiComboBox) parent.get("data");
                return (Enum) clazz.getEnumConstants()[box.getIndex()];
            }
            
            @Override
            public Enum set(ConfigKeyField key, Enum value) {
                return value;
            }
            
            @Override
            public Enum createPrimitiveDefault(Class clazz) {
                return null;
            }
        });
        
        registerSpecialType((x) -> List.class.isAssignableFrom(x) || x == ArrayList.class, new ConfigTypeConveration<List>() {
            
            private ConfigHolderObject constructHolder(Dist side, Object value) {
                return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
            }
            
            private Object constructEmpty(Class clazz) {
                try {
                    Constructor con = clazz.getConstructor();
                    return con.newInstance();
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            
            @Override
            public List readElement(List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
                if (element.isJsonArray()) {
                    JsonArray array = (JsonArray) element;
                    Class clazz = getListType(key);
                    List list = new ArrayList<>(array.size());
                    ConfigTypeConveration conversation = getUnsafe(clazz);
                    for (int i = 0; i < array.size(); i++)
                        if (conversation != null)
                            list.add(conversation.readElement(conversation.createPrimitiveDefault(clazz), loadDefault, ignoreRestart, array.get(i), side, null));
                        else {
                            Object value = constructEmpty(clazz);
                            holderConveration.readElement(constructHolder(side, value), loadDefault, ignoreRestart, array.get(i), side, null);
                            list.add(value);
                        }
                    return list;
                }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
                JsonArray array = new JsonArray();
                Class clazz = getListType(key);
                ConfigTypeConveration conversation = getUnsafe(clazz);
                for (int i = 0; i < value.size(); i++)
                    if (conversation != null)
                        array.add(conversation.writeElement(value.get(i), null, saveDefault, ignoreRestart, side, key));
                    else
                        array.add(holderConveration.writeElement(constructHolder(side, value.get(i)), null, saveDefault, ignoreRestart, side, key));
                return array;
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
                parent.setHeight(160);
                GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.getWidth() - 10, 130, true, new ArrayList<>());
                parent.add(listBox);
                
                Class subClass = getListType(key);
                ConfigTypeConveration converation = getUnsafe(subClass);
                
                int parentWidth = parent.getWidth() - 10;
                
                parent.add(new GuiButton("add", 0, 140, x -> {
                    GuiConfigSubControl control;
                    if (converation != null) {
                        control = new GuiConfigSubControl("" + 0, 2, 0, parentWidth, 14);
                        converation.createControls(control, null, subClass, control.getWidth() - 35);
                    } else {
                        Object value = constructEmpty(subClass);
                        ConfigHolderObject holder = constructHolder(Dist.DEDICATED_SERVER, value);
                        control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, holder, value);
                        ((GuiConfigSubControlHolder) control).createControls();
                    }
                    listBox.addItem(control);
                }).setTitle(new TranslationTextComponent("gui.add")));
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            public void loadValue(List value, GuiParent parent, @Nullable ConfigKeyField key) {
                GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
                if (!box.isEmpty())
                    box.clearItems();
                
                Class clazz = getListType(key);
                ConfigTypeConveration converation = getUnsafe(clazz);
                
                int parentWidth = parent.getWidth() - 10;
                
                List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
                for (int i = 0; i < value.size(); i++) {
                    Object entry = value.get(i);
                    GuiConfigSubControl control;
                    if (converation != null) {
                        control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
                        converation.createControls(control, null, clazz, control.getWidth() - 35);
                        converation.loadValue(entry, control, null);
                    } else {
                        control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, constructHolder(Dist.DEDICATED_SERVER, entry), entry);
                        ((GuiConfigSubControlHolder) control).createControls();
                    }
                    controls.add(control);
                }
                
                box.addAllItems(controls);
            }
            
            @Override
            @OnlyIn(value = Dist.CLIENT)
            protected List saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
                Class subClass = getListType(key);
                ConfigTypeConveration converation = getUnsafe(subClass);
                
                GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
                List value = new ArrayList(box.size());
                for (int i = 0; i < box.size(); i++)
                    if (converation != null)
                        value.add(converation.save(box.get(i), subClass, null));
                    else {
                        ((GuiConfigSubControlHolder) box.get(i)).save();
                        value.add(((GuiConfigSubControlHolder) box.get(i)).value);
                    }
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
            
            @Override
            public List createPrimitiveDefault(Class clazz) {
                return null;
            }
            
        });
    }
    
    public abstract T createPrimitiveDefault(Class clazz);
    
    public abstract T readElement(T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key);
    
    public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key);
    
    @OnlyIn(value = Dist.CLIENT)
    public abstract void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth);
    
    @OnlyIn(value = Dist.CLIENT)
    public abstract void loadValue(T value, GuiParent parent, @Nullable ConfigKeyField key);
    
    @OnlyIn(value = Dist.CLIENT)
    protected abstract T saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key);
    
    public abstract T set(ConfigKeyField key, T value);
    
    @OnlyIn(value = Dist.CLIENT)
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
        public T readElement(T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
            return readElement(defaultValue, loadDefault, element);
        }
        
        public abstract T readElement(T defaultValue, boolean loadDefault, JsonElement element);
        
        @Override
        public JsonElement writeElement(T value, T defaultValue, boolean ignoreRestart, boolean saveDefault, Dist side, @Nullable ConfigKeyField key) {
            return writeElement(value, defaultValue, saveDefault);
        }
        
        public abstract JsonElement writeElement(T value, T defaultValue, boolean saveDefault);
        
        @Override
        @OnlyIn(value = Dist.CLIENT)
        public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
            createControls(parent, clazz, recommendedWidth);
        }
        
        @OnlyIn(value = Dist.CLIENT)
        public abstract void createControls(GuiParent parent, Class clazz, int recommendedWidth);
        
        @Override
        @OnlyIn(value = Dist.CLIENT)
        public void loadValue(T value, GuiParent parent, @Nullable ConfigKeyField key) {
            loadValue(value, parent);
        }
        
        @OnlyIn(value = Dist.CLIENT)
        public abstract void loadValue(T value, GuiParent parent);
        
        @Override
        @OnlyIn(value = Dist.CLIENT)
        protected T saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
            return saveValue(parent, clazz);
        }
        
        @OnlyIn(value = Dist.CLIENT)
        protected abstract T saveValue(GuiParent parent, Class clazz);
        
    }
    
}
