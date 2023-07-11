package team.creative.creativecore.common.config.converation;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolderDynamic;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.premade.MobEffectConfig;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.config.premade.RegistryObjectConfig;
import team.creative.creativecore.common.config.premade.SelectableConfig;
import team.creative.creativecore.common.config.premade.SoundConfig;
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
    
    private static final NumberFormat numberFormat = createFormat();
    
    private static NumberFormat createFormat() {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setGroupingUsed(false);
        return format;
    }
    
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
        
        @Override
        public void configured(Side side) {}
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {
                parent.add(new GuiStateButton("data", 0, ChatFormatting.RED + "false", ChatFormatting.GREEN + "true").setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Boolean value, GuiParent parent) {
                GuiStateButton button = (GuiStateButton) parent.get("data");
                button.setState(value ? 1 : 0);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
                boolean decimal = isDecimal(clazz);
                if (key != null) {
                    if (decimal) {
                        CreativeConfig.DecimalRange decRange = key.field.getAnnotation(CreativeConfig.DecimalRange.class);
                        if (decRange != null && decRange.slider()) {
                            parent.add(new GuiSlider("data", decRange.min(), decRange.min(), decRange.max()).setExpandableX());
                            return;
                        }
                    } else {
                        CreativeConfig.IntRange intRange = key.field.getAnnotation(CreativeConfig.IntRange.class);
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
                if (control instanceof GuiSteppedSlider) {
                    GuiSteppedSlider button = (GuiSteppedSlider) control;
                    button.setValue(value.intValue());
                } else if (control instanceof GuiSlider) {
                    GuiSlider button = (GuiSlider) control;
                    button.setValue(value.doubleValue());
                } else
                    ((GuiTextfield) control).setText(numberFormat.format(value));
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
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
                            return parseDecimal(clazz, Mth.clamp(value.doubleValue(), decRange.min(), decRange.max()));
                    } else {
                        CreativeConfig.IntRange intRange = key.field.getAnnotation(CreativeConfig.IntRange.class);
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {
                parent.add(new GuiTextfield("data").setDim(30, 8).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(String value, GuiParent parent) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                button.setText(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected String saveValue(GuiParent parent, Class clazz) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                return button.getText();
            }
            
            @Override
            public String set(ConfigKeyField key, String value) {
                return value;
            }
            
        });
        registerTypeCreator(String.class, () -> "");
        
        registerType(ResourceLocation.class, new SimpleConfigTypeConveration<ResourceLocation>() {
            
            @Override
            public ResourceLocation readElement(ResourceLocation defaultValue, boolean loadDefault, JsonElement element) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    return new ResourceLocation(element.getAsString());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(ResourceLocation value, ResourceLocation defaultValue, boolean saveDefault) {
                return new JsonPrimitive(value.toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {
                parent.add(new GuiTextfield("data").setDim(30, 8).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(ResourceLocation value, GuiParent parent) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                button.setText(value.toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected ResourceLocation saveValue(GuiParent parent, Class clazz) {
                GuiTextfield button = (GuiTextfield) parent.get("data");
                return new ResourceLocation(button.getText());
            }
            
            @Override
            public ResourceLocation set(ConfigKeyField key, ResourceLocation value) {
                return value;
            }
            
        });
        registerTypeCreator(ResourceLocation.class, () -> new ResourceLocation(""));
        
        registerType(SoundConfig.class, new ConfigTypeConveration<SoundConfig>() {
            
            @Override
            public SoundConfig readElement(SoundConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKeyField key) {
                if (element.isJsonObject())
                    return new SoundConfig(new ResourceLocation(element.getAsJsonObject().get("sound").getAsString()), element.getAsJsonObject().get("volume").getAsFloat(), element
                            .getAsJsonObject().get("pitch").getAsFloat());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(SoundConfig value, SoundConfig defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKeyField key) {
                JsonObject json = new JsonObject();
                json.addProperty("sound", value.event.toString());
                json.addProperty("volume", value.volume);
                json.addProperty("pitch", value.pitch);
                return json;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key, Class clazz) {
                parent.flow = GuiFlow.STACK_Y;
                parent.add(new GuiComboBoxMapped<ResourceLocation>("sound", new TextMapBuilder<ResourceLocation>().addComponent(BuiltInRegistries.SOUND_EVENT.keySet(), x -> {
                    if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                        return Component.literal(x.getPath());
                    return Component.literal(x.toString());
                })).setSearchbar(true));
                GuiParent hBox = new GuiParent(GuiFlow.STACK_X);
                hBox.add(new GuiLabel("volumeLabel").setTranslate("gui.volume"));
                hBox.add(new GuiSlider("volume", 1, 0, 1).setDim(40, 10));
                hBox.add(new GuiLabel("pitchLabel").setTranslate("gui.pitch"));
                hBox.add(new GuiSlider("pitch", 1, 0.5, 2).setDim(40, 10));
                parent.add(hBox);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SoundConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
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
            protected SoundConfig saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, ConfigKeyField key) {
                GuiComboBoxMapped<ResourceLocation> box = (GuiComboBoxMapped<ResourceLocation>) parent.get("sound");
                GuiSlider volume = parent.get("volume");
                GuiSlider pitch = parent.get("pitch");
                
                return new SoundConfig(box.getSelected(), (float) volume.value, (float) pitch.value);
            }
            
            @Override
            public SoundConfig set(ConfigKeyField key, SoundConfig value) {
                return value;
            }
            
        });
        registerTypeCreator(SoundConfig.class, () -> new SoundConfig(new ResourceLocation("missing")));
        
        registerType(RegistryObjectConfig.class, new ConfigTypeConveration<RegistryObjectConfig>() {
            
            @Override
            public RegistryObjectConfig readElement(RegistryObjectConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKeyField key) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
                    return new RegistryObjectConfig(defaultValue.registry, new ResourceLocation(element.getAsString()));
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(RegistryObjectConfig value, RegistryObjectConfig defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKeyField key) {
                return new JsonPrimitive(value.location.toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key, Class clazz) {
                RegistryObjectConfig value = (RegistryObjectConfig) key.getDefault();
                parent.add(new GuiComboBoxMapped<ResourceLocation>("sound", new TextMapBuilder<ResourceLocation>().addComponent(value.registry.keySet(), x -> {
                    if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                        return Component.literal(x.getPath());
                    return Component.literal(x.toString());
                })));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(RegistryObjectConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
                GuiComboBoxMapped<ResourceLocation> box = (GuiComboBoxMapped<ResourceLocation>) parent.get("sound");
                box.select(value.location);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected RegistryObjectConfig saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, ConfigKeyField key) {
                RegistryObjectConfig object = (RegistryObjectConfig) key.getDefault();
                GuiComboBoxMapped<ResourceLocation> box = (GuiComboBoxMapped<ResourceLocation>) parent.get("sound");
                
                return new RegistryObjectConfig(object.registry, box.getSelected());
            }
            
            @Override
            public RegistryObjectConfig set(ConfigKeyField key, RegistryObjectConfig value) {
                return value;
            }
            
        });
        
        registerType(SelectableConfig.class, new ConfigTypeConveration<SelectableConfig>() {
            
            @Override
            public SelectableConfig readElement(SelectableConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKeyField key) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isNumber())
                    defaultValue.select(element.getAsInt());
                else
                    defaultValue.reset();
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(SelectableConfig value, SelectableConfig defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKeyField key) {
                return new JsonPrimitive(value.getSelected());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
                SelectableConfig value = (SelectableConfig) key.get();
                configParent.setCustomData(value.getSelected());
                parent.add(new GuiComboBox("data", new TextListBuilder().add(value.getArray(), x -> x.toString())).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
                GuiComboBox box = (GuiComboBox) parent.get("data");
                box.select(value.getSelected());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void restoreDefault(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
                value.reset();
                loadValue(value, parent, configParent, key);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected SelectableConfig saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
                SelectableConfig config = (SelectableConfig) key.get();
                GuiComboBox box = (GuiComboBox) parent.get("data");
                config.select(box.getIndex());
                return config;
            }
            
            @Override
            public SelectableConfig set(ConfigKeyField key, SelectableConfig value) {
                return value;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public boolean shouldSave(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
                return value.getSelected() != (int) configParent.getCustomData();
            }
            
        });
        
        registerType(NamedList.class, new ConfigTypeNamedList());
        registerType(Permission.class, new ConfigTypePermission());
        
        registerTypeCreator(MobEffectConfig.class, () -> new MobEffectConfig(BuiltInRegistries.MOB_EFFECT, new ResourceLocation("minecraft", "slowness"), 2, 1));
        
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(ConfigHolderObject value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected ConfigHolderObject saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(ConfigHolderDynamic value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {}
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected ConfigHolderDynamic saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
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
        
        registerSpecialType((x) -> Enum.class.isAssignableFrom(x), new SimpleConfigTypeConveration<Enum>() {
            
            private static Class getEnumClass(Class clazz) {
                if (clazz.isEnum())
                    return clazz;
                return clazz.getSuperclass();
            }
            
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
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, Class clazz) {
                parent.add(new GuiComboBox("data", new TextListBuilder().add(getEnumClass(clazz).getEnumConstants(), (x) -> ((Enum) x).name())));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Enum value, GuiParent parent) {
                GuiComboBox box = (GuiComboBox) parent.get("data");
                box.select(value.ordinal());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Enum saveValue(GuiParent parent, Class clazz) {
                GuiComboBox box = (GuiComboBox) parent.get("data");
                return (Enum) getEnumClass(clazz).getEnumConstants()[box.getIndex()];
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
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void loadValue(T value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(T value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        loadValue(value, parent, configParent, key);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSave(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
        return !key.get().equals(value);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected abstract T saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key);
    
    public abstract T set(ConfigKeyField key, T value);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public T save(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        T value = saveValue(parent, configParent, clazz, key);
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
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
            createControls(parent, clazz);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void createControls(GuiParent parent, Class clazz);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void loadValue(T value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
            loadValue(value, parent);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void loadValue(T value, GuiParent parent);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected T saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
            return saveValue(parent, clazz);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected abstract T saveValue(GuiParent parent, Class clazz);
        
    }
    
}
