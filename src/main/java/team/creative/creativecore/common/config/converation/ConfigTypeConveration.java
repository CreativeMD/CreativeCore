package team.creative.creativecore.common.config.converation;

import static team.creative.creativecore.CreativeCore.LOGGER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryObject;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryObjectList;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryTag;
import team.creative.creativecore.common.config.converation.registry.ConfigTypeRegistryTagList;
import team.creative.creativecore.common.config.core.IConfigRegistry;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.gui.GuiInfoStackButton;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.key.ConfigKey;
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
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiColorPicker;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiSlider;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientBlock;
import team.creative.creativecore.common.util.math.matrix.IntMatrix3;
import team.creative.creativecore.common.util.math.matrix.IntMatrix3c;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.Color;
import team.creative.creativecore.common.util.type.list.PairList;

public abstract class ConfigTypeConveration<T> {
    
    private static final HashMap<Class, Function<ConfigField, ?>> TYPE_CREATORS = new HashMap<>();
    private static final HashMap<Class, Function<ConfigField, Type>> TYPE_GETTERS = new HashMap<>();
    private static final HashMap<Class, Function<ConfigField, ?>> COLLECTION_CREATORS = new HashMap<>();
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
        public ConfigKey getField(String key) {
            return null;
        }
        
        @Override
        public Object get(String key) {
            return null;
        }
        
        @Override
        public Collection<? extends ConfigKey> fields() {
            return Collections.EMPTY_LIST;
        }
        
        @Override
        public void configured(Side side) {}
        
        @Override
        public IConfigRegistry getRegistry() {
            throw new UnsupportedOperationException("This fake parent should never be used to get the registry");
        }
        
    };
    
    public static boolean isTypeCreator(Class clazz) {
        return TYPE_CREATORS.containsKey(clazz);
    }
    
    public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Function<ConfigField, T> type) {
        TYPE_CREATORS.put(clazz, type);
    }
    
    public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Supplier<T> type) {
        TYPE_CREATORS.put(clazz, x -> type.get());
    }
    
    public static void registerTypeGetter(Class clazz, Function<ConfigField, Type> getter) {
        TYPE_GETTERS.put(clazz, getter);
    }
    
    public static <T, U extends T> void registerCollectionCreator(Class<U> clazz, Function<ConfigField, T> getter) {
        COLLECTION_CREATORS.put(clazz, getter);
    }
    
    public static <T, U extends T> ConfigTypeConveration<T> registerType(Class<U> clazz, ConfigTypeConveration<T> type) {
        TYPES.put(clazz, type);
        return type;
    }
    
    public static <T> void registerTypes(ConfigTypeConveration<T> type, Class<? extends T>... classes) {
        for (int i = 0; i < classes.length; i++)
            TYPES.put(classes[i], type);
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
    
    public static Object createObject(ConfigField field) {
        Function func = TYPE_CREATORS.get(field.getType());
        if (func != null)
            return func.apply(field);
        try {
            return field.getType().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {}
        return null;
    }
    
    public static Type getGenericType(ConfigField field) {
        Function<ConfigField, Type> func = TYPE_GETTERS.get(field.getType());
        if (func != null)
            return func.apply(field);
        Type type = field.getGenericType();
        if (type instanceof Class)
            return type;
        if (type instanceof ParameterizedType p)
            return p.getActualTypeArguments()[0];
        throw new UnsupportedOperationException("This type is not supported " + type);
    }
    
    public static Type getGenericType(ConfigKey key) {
        return getGenericType(key.field());
    }
    
    public static Object createCollection(ConfigField field) {
        Function func = COLLECTION_CREATORS.get(field.getType());
        if (func != null)
            return func.apply(field);
        return null;
    }
    
    public static Object createCollection(ConfigKey key) {
        return createCollection(key.field());
    }
    
    static {
        ConfigTypeNumber.init();
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
            public SoundConfig readElement(HolderLookup.Provider provider, SoundConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side,
                    ConfigKey key) {
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
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                parent.flow = GuiFlow.STACK_Y;
                parent.add(new GuiComboBox<>("sound", new TextMapBuilder<ResourceLocation>().addComponent(BuiltInRegistries.SOUND_EVENT.keySet(), x -> {
                    if (x.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                        return Component.literal(x.getPath());
                    return Component.literal(x.toString());
                })).setSearchbar(true));
                GuiParent hBox = new GuiParent(GuiFlow.STACK_X).add(new GuiLabel("volumeLabel").setTranslate("gui.volume")).add(new GuiSlider("volume", 1, 0, 1).setDim(40, 10))
                        .add(new GuiLabel("pitchLabel").setTranslate("gui.pitch")).add(new GuiSlider("pitch", 1, 0.5, 2).setDim(40, 10));
                parent.add(hBox);
                hBox.add(new GuiButton("play", x -> {
                    GuiComboBox<ResourceLocation> box = parent.get("sound");
                    GuiSlider volume = parent.get("volume");
                    GuiSlider pitch = parent.get("pitch");
                    var sound = BuiltInRegistries.SOUND_EVENT.get(box.selected());
                    if (sound != null)
                        GuiControl.playSound(sound, (float) volume.getValue(), (float) pitch.getValue());
                }).setTranslate("gui.play"));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SoundConfig value, SoundConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                GuiComboBox<ResourceLocation> box = parent.get("sound");
                GuiSlider volume = parent.get("volume");
                GuiSlider pitch = parent.get("pitch");
                
                box.select(value.event);
                volume.setValue(value.volume);
                pitch.setValue(value.pitch);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected SoundConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                GuiComboBox<ResourceLocation> box = parent.get("sound");
                GuiSlider volume = parent.get("volume");
                GuiSlider pitch = parent.get("pitch");
                
                return new SoundConfig(box.selected(), (float) volume.getValue(), (float) pitch.getValue());
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
            public SelectableConfig readElement(HolderLookup.Provider provider, SelectableConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element,
                    Side side, ConfigKey key) {
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
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                SelectableConfig value = (SelectableConfig) key.get();
                configParent.setCustomData(value.getSelected());
                parent.add(new GuiComboBox("data", new TextMapBuilder().addComponent(value.getArray(), x -> Component.literal(x.toString()))).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(SelectableConfig value, SelectableConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                GuiComboBox box = parent.get("data");
                box.select(value.getSelected());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void restoreDefault(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                value.reset();
                loadValue(value, value, parent, configParent, key, side);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected SelectableConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                SelectableConfig config = (SelectableConfig) key.get();
                GuiComboBox box = parent.get("data");
                config.select(box.selectedIndex());
                return config;
            }
            
            @Override
            public SelectableConfig set(ConfigKey key, SelectableConfig value) {
                return value;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public boolean shouldSave(SelectableConfig value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                return value.getSelected() != (int) configParent.getCustomData();
            }
            
        });
        
        registerType(NamedList.class, new ConfigTypeNamedList());
        registerType(Permission.class, new ConfigTypePermission());
        
        registerTypeCreator(MobEffectConfig.class, () -> new MobEffectConfig(BuiltInRegistries.MOB_EFFECT, ResourceLocation.withDefaultNamespace("slowness"), 2, 1, false));
        
        registerType(ToggleableConfig.class, new ConfigTypeToggleable());
        
        registerTypes(new SimpleConfigTypeConveration<IntMatrix3c>() {
            
            @Override
            public IntMatrix3c readElement(ConfigKey key, IntMatrix3c defaultValue, Side side, JsonElement element) {
                if (element instanceof JsonArray a && a.size() == 9) {
                    int[] array = new int[9];
                    for (int i = 0; i < array.length; i++)
                        array[i] = a.get(i).getAsInt();
                    return new IntMatrix3(array);
                }
                return new IntMatrix3(defaultValue);
            }
            
            @Override
            public JsonElement writeElement(IntMatrix3c value, ConfigKey key, Side side) {
                JsonArray json = new JsonArray(9);
                int[] array = value.getAsArray();
                for (int i = 0; i < array.length; i++)
                    json.add(array[i]);
                return json;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.flow = GuiFlow.STACK_Y;
                GuiParent r = new GuiParent();
                parent.add(r);
                r.add(new GuiTextfield("m00").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m01").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m02").setNumbersIncludingNegativeOnly());
                r = new GuiParent();
                parent.add(r);
                r.add(new GuiTextfield("m10").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m11").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m12").setNumbersIncludingNegativeOnly());
                r = new GuiParent();
                parent.add(r);
                r.add(new GuiTextfield("m20").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m21").setNumbersIncludingNegativeOnly());
                r.add(new GuiTextfield("m22").setNumbersIncludingNegativeOnly());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(IntMatrix3c value, GuiParent parent) {
                parent.get("m00", GuiTextfield.class).setText("" + value.m00());
                parent.get("m01", GuiTextfield.class).setText("" + value.m01());
                parent.get("m02", GuiTextfield.class).setText("" + value.m02());
                
                parent.get("m10", GuiTextfield.class).setText("" + value.m10());
                parent.get("m11", GuiTextfield.class).setText("" + value.m11());
                parent.get("m12", GuiTextfield.class).setText("" + value.m12());
                
                parent.get("m20", GuiTextfield.class).setText("" + value.m20());
                parent.get("m21", GuiTextfield.class).setText("" + value.m21());
                parent.get("m22", GuiTextfield.class).setText("" + value.m22());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected IntMatrix3c saveValue(GuiParent parent, ConfigKey key) {
                return new IntMatrix3(parent.get("m00", GuiTextfield.class).parseInteger(), parent.get("m01", GuiTextfield.class).parseInteger(), parent.get("m02",
                    GuiTextfield.class).parseInteger(), parent.get("m10", GuiTextfield.class).parseInteger(), parent.get("m11", GuiTextfield.class).parseInteger(), parent.get(
                        "m12", GuiTextfield.class).parseInteger(), parent.get("m20", GuiTextfield.class).parseInteger(), parent.get("m21", GuiTextfield.class)
                                .parseInteger(), parent.get("m22", GuiTextfield.class).parseInteger());
            }
            
            @Override
            public IntMatrix3c set(ConfigKey key, IntMatrix3c value) {
                return value;
            }
            
        }, IntMatrix3c.class, IntMatrix3.class);
        
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
                    return Enum.valueOf(defaultValue.getDeclaringClass(), element.getAsString());
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
                parent.add(new GuiComboBox<>("data", new TextMapBuilder<>().addComponent(getEnumClass(key.field().getType()).getEnumConstants(), (x) -> Component.literal(((Enum) x)
                        .name()))));
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
                return (Enum) box.selected();
            }
            
            @Override
            public Enum set(ConfigKey key, Enum value) {
                return value;
            }
        });
        
        registerSpecialType((x) -> List.class.isAssignableFrom(x) || x == ArrayList.class, new ConfigTypeList());
        
        final CreativeIngredient temp = new CreativeIngredientBlock(Blocks.DIRT);
        ConfigTypeConveration.registerSpecialType(CreativeIngredient.class::isAssignableFrom, new ConfigTypeConveration<CreativeIngredient>() {
            
            @Override
            public CreativeIngredient readElement(HolderLookup.Provider provider, CreativeIngredient defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element,
                    Side side, ConfigKey key) {
                if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
                    try {
                        return CreativeIngredient.load(provider, TagParser.parseTag(element.getAsString()));
                    } catch (CommandSyntaxException e) {
                        LOGGER.error(e);
                    }
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(HolderLookup.Provider provider, CreativeIngredient value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
                return new JsonPrimitive(value.save(provider).toString());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                parent.add(new GuiInfoStackButton("data", temp).setExpandableX());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(CreativeIngredient value, CreativeIngredient defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                GuiInfoStackButton button = parent.get("data");
                button.set(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected CreativeIngredient saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
                GuiInfoStackButton button = parent.get("data");
                return button.get();
            }
            
            @Override
            public CreativeIngredient set(ConfigKey key, CreativeIngredient value) {
                return value;
            }
        });
        ConfigTypeConveration.registerTypeCreator(CreativeIngredient.class, () -> new CreativeIngredientBlock(Blocks.DIRT));
        
        ConfigTypeConveration.registerType(Color.class, new SimpleConfigTypeConveration<Color>() {
            
            @Override
            public Color readElement(ConfigKey key, Color defaultValue, Side side, JsonElement element) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
                    return new Color(element.getAsInt());
                return defaultValue;
            }
            
            @Override
            public JsonElement writeElement(Color value, ConfigKey key, Side side) {
                return new JsonPrimitive(value.toInt());
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiColorPicker("color", new Color(), true, 0));
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            public void loadValue(Color value, GuiParent parent) {
                GuiColorPicker picker = parent.get("color");
                picker.setColor(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            @OnlyIn(Dist.CLIENT)
            protected Color saveValue(GuiParent parent, ConfigKey key) {
                GuiColorPicker picker = parent.get("color");
                return new Color(picker.color);
            }
            
            @Override
            public Color set(ConfigKey key, Color value) {
                return value;
            }
        });
        ConfigTypeConveration.registerTypeCreator(Color.class, () -> new Color());
        
        ConfigTypeConverationSided.registerSide();
    }
    
    public abstract T readElement(HolderLookup.Provider provider, T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key);
    
    public abstract JsonElement writeElement(HolderLookup.Provider provider, T value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        loadValue(value, value, parent, configParent, key, side);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSave(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        return !key.get().equals(value);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected abstract T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side);
    
    public abstract T set(ConfigKey key, T value);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public T save(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        T value = saveValue(parent, configParent, key, side);
        if (value != null && key != null)
            return set(key, value);
        return value;
    }
    
    public boolean areEqual(T one, T two, ConfigKey key, Side side) {
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
        public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
            createControls(parent, key);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void createControls(GuiParent parent, ConfigKey key);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
            loadValue(value, parent);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        public abstract void loadValue(T value, GuiParent parent);
        
        @Override
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
            return saveValue(parent, key);
        }
        
        @Environment(EnvType.CLIENT)
        @OnlyIn(Dist.CLIENT)
        protected abstract T saveValue(GuiParent parent, ConfigKey key);
        
    }
    
}
