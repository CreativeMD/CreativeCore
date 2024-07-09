package team.creative.creativecore.common.config.converation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryObject;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryObjectList;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryTag;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryTagList;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyField;
import team.creative.creativecore.common.config.premade.MobEffectConfig;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.config.premade.SelectableConfig;
import team.creative.creativecore.common.config.premade.SoundConfig;
import team.creative.creativecore.common.config.premade.ToggleableConfig;
import team.creative.creativecore.common.config.premade.registry.RegistryObjectConfig;
import team.creative.creativecore.common.config.premade.registry.RegistryObjectListConfig;
import team.creative.creativecore.common.config.premade.registry.RegistryTagConfig;
import team.creative.creativecore.common.config.premade.registry.RegistryTagListConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBox;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiSlider;
import team.creative.creativecore.common.gui.controls.simple.GuiStateButton;
import team.creative.creativecore.common.gui.controls.simple.GuiSteppedSlider;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.text.TextListBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.list.PairList;

public abstract class ConfigTypeConveration<T> {
    
    private static final NumberFormat NUMBER_FORMAT = createFormat();
    
    private static NumberFormat createFormat() {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setGroupingUsed(false);
        return format;
    }
    
    private static final HashMap<Class, Function<ConfigKey, ?>> TYPE_CREATORS = new HashMap<>();
    private static final HashMap<Class, Function<ConfigKey, Type>> TYPE_GETTERS = new HashMap<>();
    private static final HashMap<Class, Function<ConfigKey, ?>> COLLECTION_CREATORS = new HashMap<>();
    private static final HashMap<Class, ConfigTypeConveration> TYPES = new HashMap<>();
    private static final PairList<Predicate<Class>, ConfigTypeConveration> SPECIAL_TYPES = new PairList<>();
    
    public static final ICreativeConfigHolder FAKE_PARENT = new ICreativeConfigHolder() {
        
        @Override
        public ConfigSynchronization synchronization() {
            return ConfigSynchronization.UNIVERSAL;
        }
        
        @Override
        public JsonObject save(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
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
        public void load(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {}
        
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
        public ConfigKeyField getField(String key) {
            return null;
        }
        
        @Override
        public Object get(String key) {
            return null;
        }
        
        @Override
        public Collection<? extends ConfigKeyField> fields() {
            return null;
        }
        
        @Override
        public void configured(Side side) {}
    };
    
    public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Function<ConfigKey, T> type) {
        TYPE_CREATORS.put(clazz, type);
    }
    
    public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Supplier<T> type) {
        TYPE_CREATORS.put(clazz, x -> type.get());
    }
    
    public static void registerTypeGetter(Class clazz, Function<ConfigKey, Type> getter) {
        TYPE_GETTERS.put(clazz, getter);
    }
    
    public static <T, U extends T> void registerCollectionCreator(Class<U> clazz, Function<ConfigKey, T> getter) {
        COLLECTION_CREATORS.put(clazz, getter);
    }
    
    public static <T, U extends T> ConfigTypeConveration<T> registerType(Class<U> clazz, ConfigTypeConveration<T> type) {
        TYPES.put(clazz, type);
        return type;
    }
    
    public static void registerSpecialType(Predicate<Class> predicate, ConfigTypeConveration type) {
        SPECIAL_TYPES.add(predicate, type);
    }
    
    public static boolean has(Class typeClass) {
        if (TYPES.containsKey(typeClass))
            return true;
        
        if (typeClass.isAnnotationPresent(CreativeConfig.class))
            return false;
        
        for (int i = 0; i < SPECIAL_TYPES.size(); i++)
            if (SPECIAL_TYPES.get(i).key.test(typeClass))
                return true;
            
        return false;
    }
    
    public static ConfigTypeConveration get(Class typeClass) {
        ConfigTypeConveration converation = TYPES.get(typeClass);
        if (converation != null)
            return converation;
        for (int i = 0; i < SPECIAL_TYPES.size(); i++)
            if (SPECIAL_TYPES.get(i).key.test(typeClass))
                return SPECIAL_TYPES.get(i).value;
        throw new RuntimeException("Could not find converation for " + typeClass.getName());
    }
    
    public static ConfigTypeConveration getUnsafe(Class typeClass) {
        ConfigTypeConveration converation = TYPES.get(typeClass);
        if (converation != null)
            return converation;
        for (int i = 0; i < SPECIAL_TYPES.size(); i++)
            if (SPECIAL_TYPES.get(i).key.test(typeClass))
                return SPECIAL_TYPES.get(i).value;
        return null;
    }
    
    public static Object createObject(ConfigKey key) {
        Function func = TYPE_CREATORS.get(key.getType());
        if (func != null)
            return func.apply(key);
        try {
            return key.getType().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {}
        return null;
    }
    
    public static Type getGenericType(ConfigKey key) {
        Function<ConfigKey, Type> func = TYPE_GETTERS.get(key.getType());
        if (func != null)
            return func.apply(key);
        ParameterizedType type = (ParameterizedType) key.getGenericType();
        return type.getActualTypeArguments()[0];
    }
    
    public static Object createCollection(ConfigKey key) {
        Function func = COLLECTION_CREATORS.get(key.getType());
        if (func != null)
            return func.apply(key);
        return null;
    }
    
    static {
        ConfigTypeConveration<Boolean> booleanType = new SimpleConfigTypeConveration<Boolean>() {
            
            @Override
            public Boolean readElement(ConfigKey key, Boolean defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isBoolean())
                    return element.getAsBoolean();
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Boolean value, ConfigKey key, Side side) {
                return new JsonPrimitive(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiStateButton("data", 0, ChatFormatting.RED + "false", ChatFormatting.GREEN + "true").setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Boolean value, GuiParent parent) {
                GuiStateButton button = parent.get("data");
                button.setState(value ? 1 : 0);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Boolean saveValue(GuiParent parent, ConfigKey key) {
                GuiStateButton button = parent.get("data");
                return button.getState() == 1;
            }
            
            @Override
            public Boolean set(ConfigKey key, Boolean value) {
                return value;
            }
            
        };
        registerType(boolean.class, booleanType);
        registerType(Boolean.class, booleanType);
        
        registerTypeCreator(boolean.class, () -> false);
        registerTypeCreator(Boolean.class, () -> Boolean.FALSE);
        
        ConfigTypeConveration<Number> numberType = new SimpleConfigTypeConveration<Number>() {
            
            @Override
            public Number readElement(ConfigKey key, Number defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber()) {
                    Class clazz = key.getType();
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
            public JsonElement writeElement(Number value, ConfigKey key, Side side) {
                return new JsonPrimitive(value);
            }
            
            public boolean isDecimal(Class clazz) {
                return clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                boolean decimal = isDecimal(key.getType());
                if (key != null) {
                    if (decimal) {
                        CreativeConfig.DecimalRange decRange = key.getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null && decRange.slider()) {
                            parent.add(new GuiSlider("data", decRange.min(), decRange.min(), decRange.max()).setExpandableX());
                            return;
                        }
                    } else {
                        CreativeConfig.IntRange intRange = key.getAnnotation(CreativeConfig.IntRange.class);
                        if (intRange != null && intRange.slider()) {
                            parent.add(new GuiSteppedSlider("data", intRange.min(), intRange.min(), intRange.max()).setExpandableX());
                            return;
                        }
                    }
                }
                
                GuiTextfield textfield = (GuiTextfield) new GuiTextfield("data").setDim(30, 8).setExpandableX();
                if (decimal)
                    textfield.setFloatOnly();
                else
                    textfield.setNumbersIncludingNegativeOnly();
                parent.add(textfield);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Number value, GuiParent parent) {
                GuiControl control = parent.get("data");
                if (control instanceof GuiSteppedSlider button)
                    button.setValue(value.intValue());
                else if (control instanceof GuiSlider button)
                    button.setValue(value.doubleValue());
                else
                    ((GuiTextfield) control).setText(NUMBER_FORMAT.format(value));
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
                        return 0;
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Number saveValue(GuiParent parent, ConfigKey key) {
                GuiControl control = parent.get("data");
                String text;
                if (control instanceof GuiSteppedSlider button) {
                    text = "" + button.getIntValue();
                } else if (control instanceof GuiSlider button) {
                    text = "" + button.getValue();
                } else
                    text = ((GuiTextfield) control).getText();
                return parseNumber(key.getType(), text);
            }
            
            @Override
            public Number set(ConfigKey key, Number value) {
                if (key != null) {
                    Class clazz = key.getType();
                    if (isDecimal(clazz)) {
                        CreativeConfig.DecimalRange decRange = key.getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null)
                            return parseDecimal(clazz, Mth.clamp(value.doubleValue(), decRange.min(), decRange.max()));
                    } else {
                        CreativeConfig.IntRange intRange = key.getAnnotation(CreativeConfig.IntRange.class);
                        if (intRange != null)
                            return parseInt(clazz, Mth.clamp(value.intValue(), intRange.min(), intRange.max()));
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
        registerTypeCreator(Byte.class, () -> Byte.valueOf((byte) 0));
        registerTypeCreator(short.class, () -> (short) 0);
        registerTypeCreator(Short.class, () -> Short.valueOf((short) 0));
        registerTypeCreator(int.class, () -> 0);
        registerTypeCreator(Integer.class, () -> Integer.valueOf(0));
        registerTypeCreator(long.class, () -> (long) 0);
        registerTypeCreator(Long.class, () -> Long.valueOf(0));
        registerTypeCreator(float.class, () -> 0F);
        registerTypeCreator(Float.class, () -> Float.valueOf(0));
        registerTypeCreator(double.class, () -> 0D);
        registerTypeCreator(Double.class, () -> Double.valueOf(0));
        
        registerType(String.class, new SimpleConfigTypeConveration<String>() {
            
            @Override
            public String readElement(ConfigKey key, String defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    return element.getAsString();
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(String value, ConfigKey key, Side side) {
                return new JsonPrimitive(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiTextfield("data").setDim(30, 8).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(String value, GuiParent parent) {
                GuiTextfield button = parent.get("data");
                button.setText(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected String saveValue(GuiParent parent, ConfigKey key) {
                GuiTextfield button = parent.get("data");
                return button.getText();
            }
            
            @Override
            public String set(ConfigKey key, String value) {
                return value;
            }
            
        });
        registerTypeCreator(String.class, () -> "");
        
        registerType(ResourceLocation.class, new SimpleConfigTypeConveration<>() {
            
            @Override
            public ResourceLocation readElement(ConfigKey key, ResourceLocation defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    return ResourceLocation.parse(element.getAsString());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ResourceLocation value, ConfigKey key, Side side) {
                return new JsonPrimitive(value.toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiTextfield("data").setDim(30, 8).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(ResourceLocation value, GuiParent parent) {
                GuiTextfield button = parent.get("data");
                button.setText(value.toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected ResourceLocation saveValue(GuiParent parent, ConfigKey key) {
                GuiTextfield button = parent.get("data");
                return ResourceLocation.parse(button.getText());
            }
            
            @Override
            public ResourceLocation set(ConfigKey key, ResourceLocation value) {
                return value;
            }
            
        });
        registerTypeCreator(ResourceLocation.class, () -> ResourceLocation.withDefaultNamespace(""));
        
        registerType(SoundConfig.class, new ConfigTypeConveration<SoundConfig>() {
            
            @Override
            public SoundConfig readElement(HolderLookup.Provider provider, SoundConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
                if (element.isJsonObject())
                    return new SoundConfig(ResourceLocation.parse(element.getAsJsonObject().get("sound").getAsString()), element.getAsJsonObject().get("volume")
                            .getAsFloat(), element.getAsJsonObject().get("pitch").getAsFloat());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(HolderLookup.Provider provider, SoundConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
                JsonObject json = new JsonObject();
                json.addProperty("sound", value.event.toString());
                json.addProperty("volume", value.volume);
                json.addProperty("pitch", value.pitch);
                return json;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                parent.flow = GuiFlow.STACK_Y;
                parent.add(new GuiComboBoxMapped<>("sound", new TextMapBuilder<ResourceLocation>().addComponent(BuiltInRegistries.SOUND_EVENT.keySet(), x -> {
                    if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                        return Component.literal(x.getPath());
                    return Component.literal(x.toString());
                })).setSearchbar(true));
                GuiParent hBox = new GuiParent(GuiFlow.STACK_X).add(new GuiLabel("volumeLabel").setTranslate("gui.volume")).add(new GuiSlider("volume", 1, 0, 1).setDim(40, 10))
                        .add(new GuiLabel("pitchLabel").setTranslate("gui.pitch")).add(new GuiSlider("pitch", 1, 0.5, 2).setDim(40, 10));
                parent.add(hBox);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SoundConfig value, SoundConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                GuiComboBoxMapped<ResourceLocation> box = parent.get("sound");
                GuiSlider volume = parent.get("volume");
                GuiSlider pitch = parent.get("pitch");
                
                box.select(value.event);
                volume.setValue(value.volume);
                pitch.setValue(value.pitch);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected SoundConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                GuiComboBoxMapped<ResourceLocation> box = parent.get("sound");
                GuiSlider volume = parent.get("volume");
                GuiSlider pitch = parent.get("pitch");
                
                return new SoundConfig(box.getSelected(), (float) volume.getValue(), (float) pitch.getValue());
            }
            
            @Override
            public SoundConfig set(ConfigKey key, SoundConfig value) {
                return value;
            }
            
        });
        registerTypeCreator(SoundConfig.class, () -> new SoundConfig(ResourceLocation.withDefaultNamespace("missing")));
        
        registerType(RegistryObjectConfig.class, new ConfigTypeRegistryObject());
        registerType(RegistryObjectListConfig.class, new ConfigTypeRegistryObjectList());
        
        registerType(RegistryTagConfig.class, new ConfigTypeRegistryTag());
        registerType(RegistryTagListConfig.class, new ConfigTypeRegistryTagList());
        
        registerType(SelectableConfig.class, new ConfigTypeConveration<SelectableConfig>() {
            
            @Override
            public SelectableConfig readElement(HolderLookup.Provider provider, SelectableConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber())
                    defaultValue.select(element.getAsInt());
                else
                    defaultValue.reset();
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(HolderLookup.Provider provider, SelectableConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
                return new JsonPrimitive(value.getSelected());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                SelectableConfig value = (SelectableConfig) key.get();
                configParent.setCustomData(value.getSelected());
                parent.add(new GuiComboBox("data", new TextListBuilder().add(value.getArray(), Object::toString)).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SelectableConfig value, SelectableConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                GuiComboBox box = parent.get("data");
                box.select(value.getSelected());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void restoreDefault(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                value.reset();
                loadValue(value, value, parent, configParent, key);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected SelectableConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                SelectableConfig config = (SelectableConfig) key.get();
                GuiComboBox box = parent.get("data");
                config.select(box.getIndex());
                return config;
            }
            
            @Override
            public SelectableConfig set(ConfigKey key, SelectableConfig value) {
                return value;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public boolean shouldSave(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
                return value.getSelected() != (int) configParent.getCustomData();
            }
            
        });
        
        registerType(NamedList.class, new ConfigTypeNamedList());
        registerType(Permission.class, new ConfigTypePermission());
        
        registerTypeCreator(MobEffectConfig.class, () -> new MobEffectConfig(BuiltInRegistries.MOB_EFFECT, ResourceLocation.withDefaultNamespace("slowness"), 2, 1, false));
        
        registerType(ToggleableConfig.class, new ConfigTypeToggleable());
        
        registerSpecialType((x) -> {
            if (x.isArray()) {
                if (has(x.getComponentType()))
                    return true;
                throw new RuntimeException("Array with holders are not permitted");
            }
            return false;
        }, new ConfigTypeArray());
        
        registerSpecialType(Enum.class::isAssignableFrom, new SimpleConfigTypeConveration<Enum>() {
            
            private static Class getEnumClass(Class clazz) {
                if (clazz.isEnum())
                    return clazz;
                return clazz.getSuperclass();
            }
            
            @Override
            public Enum readElement(ConfigKey key, Enum defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    return Enum.valueOf(key.getType().getDeclaringClass(), element.getAsString());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Enum value, ConfigKey key, Side side) {
                return new JsonPrimitive(value.name());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiComboBox("data", new TextListBuilder().add(getEnumClass(key.getType()).getEnumConstants(), (x) -> ((Enum) x).name())));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Enum value, GuiParent parent) {
                GuiComboBox box = parent.get("data");
                box.select(value.ordinal());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Enum saveValue(GuiParent parent, ConfigKey key) {
                GuiComboBox box = parent.get("data");
                return (Enum) getEnumClass(key.getType()).getEnumConstants()[box.getIndex()];
            }
            
            @Override
            public Enum set(ConfigKey key, Enum value) {
                return value;
            }
        });
        
        registerSpecialType((x) -> List.class.isAssignableFrom(x) || x == ArrayList.class, new ConfigTypeList());
    }
    
    public abstract T readElement(HolderLookup.Provider provider, T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key);
    
    public abstract JsonElement writeElement(HolderLookup.Provider provider, T value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        loadValue(value, value, parent, configParent, key);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSave(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        return !key.get().equals(value);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected abstract T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key);
    
    public abstract T set(ConfigKey key, T value);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public T save(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        T value = saveValue(parent, configParent, key);
        if (value != null && key != null)
            return set(key, value);
        return value;
    }
    
    public boolean areEqual(T one, T two, ConfigKey key) {
        return one.equals(two);
    }
    
    public static abstract class SimpleConfigTypeConveration<T> extends ConfigTypeConveration<T> {
        
        @Override
        public T readElement(HolderLookup.Provider provider, T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
            return readElement(key, defaultValue, side, element);
        }
        
        public abstract T readElement(ConfigKey key, T defaultValue, Side side, JsonElement element);
        
        @Override
        public JsonElement writeElement(HolderLookup.Provider provider, T value, boolean ignoreRestart, boolean saveDefault, Side side, ConfigKey key) {
            return writeElement(value, key, side);
        }
        
        public abstract JsonElement writeElement(T value, ConfigKey key, Side side);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
            createControls(parent, key);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void createControls(GuiParent parent, ConfigKey key);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
            loadValue(value, parent);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void loadValue(T value, GuiParent parent);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
            return saveValue(parent, key);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected abstract T saveValue(GuiParent parent, ConfigKey key);
        
    }
    
}
