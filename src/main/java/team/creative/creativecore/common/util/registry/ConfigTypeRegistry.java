package team.creative.creativecore.common.util.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.core.ConfigEqualChecker;
import team.creative.creativecore.common.config.core.ICreativeRegistry;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlNested;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.util.mc.JsonUtils;
import team.creative.creativecore.common.util.mc.NBTUtils;

public class ConfigTypeRegistry<T> implements ICreativeRegistry {
    
    public static final Predicate<Field> DEFAULT_FIELD_PREDICATE = x -> Modifier.isPublic(x.getModifiers()) && !Modifier.isTransient(x.getModifiers());
    private final HashMap<Class<? extends T>, ConfigurationType> types = new LinkedHashMap<>();
    private final HashMap<String, ConfigurationType> loaders = new LinkedHashMap<>();
    private final ConfigEqualChecker equalChecker = new ConfigEqualChecker();
    private ConfigurationType defaultType;
    private Predicate<Field> fieldPredicate = DEFAULT_FIELD_PREDICATE;
    private boolean keepUnrelatedData = true;
    
    public ConfigTypeRegistry<T> setFieldPredicate(Predicate<Field> fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
        return this;
    }
    
    /** if set to true switching between types will keep the old information in there when saving it to nbt */
    public ConfigTypeRegistry<T> setKeepUnrelatedData(boolean keep) {
        this.keepUnrelatedData = keep;
        return this;
    }
    
    public <C extends T> void register(String id, Class<C> clazz, C defaultReference, Supplier<C> factory) {
        if (types.containsKey(clazz))
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " is already registered!");
        ConfigurationType type = new ConfigurationType(id, clazz, defaultReference, factory);
        types.put(clazz, type);
        loaders.put(id, type);
    }
    
    protected ConfigurationType get(T data) {
        return types.get(data.getClass());
    }
    
    public boolean contains(Class<? extends T> clazz) {
        return types.containsKey(clazz);
    }
    
    public String getId(Class<? extends T> clazz) {
        return types.get(clazz).id;
    }
    
    public T load(HolderLookup.Provider provider, CompoundTag nbt, Side side) {
        ConfigurationType type = loaders.getOrDefault(nbt.getString("type"), defaultType);
        return type.load(provider, nbt, side);
    }
    
    public T loadOrCreateDefault(HolderLookup.Provider provider, CompoundTag nbt, String id, Side side) {
        if (nbt.getString("type").equals(id))
            return load(provider, nbt, side);
        return createDefault(id);
    }
    
    public CompoundTag save(HolderLookup.Provider provider, T data, CompoundTag nbt, Side side) {
        var type = get(data);
        return type.save(provider, data, nbt, side);
    }
    
    public GuiConfigSubControlNested create(T data, Side side) {
        var type = get(data);
        return new GuiConfigSubControlNested("", type.create(side, data), data, side, null);
    }
    
    public T createDefault(String id) {
        return loaders.get(id).factory.get();
    }
    
    @Override
    public ConfigEqualChecker getEqualChecker() {
        return equalChecker;
    }
    
    @Override
    public boolean is(Field field) {
        return fieldPredicate.test(field);
    }
    
    private class ConfigurationType {
        
        private final String id;
        private final Supplier<? extends T> factory;
        private final T defaultReference;
        
        public ConfigurationType(String id, Class<? extends T> clazz, T defaultReference, Supplier<? extends T> factory) {
            this.id = id;
            this.factory = factory;
            this.defaultReference = defaultReference;
        }
        
        public ConfigHolderObject create(Side side, T data) {
            return ConfigHolderObject.createUnrelated(ConfigTypeRegistry.this, side, data, defaultReference);
        }
        
        public CompoundTag save(HolderLookup.Provider provider, T data, CompoundTag nbt, Side side) {
            nbt = NBTUtils.of(create(side, data).save(provider, false, true, side), keepUnrelatedData ? nbt : new CompoundTag());
            nbt.putString("t", id);
            return nbt;
        }
        
        public T load(HolderLookup.Provider provider, CompoundTag nbt, Side side) {
            T data = factory.get();
            create(side, data).load(provider, false, true, JsonUtils.of(nbt), side);
            return data;
        }
        
    }
    
}
