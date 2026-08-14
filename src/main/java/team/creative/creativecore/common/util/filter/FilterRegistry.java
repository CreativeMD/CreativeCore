package team.creative.creativecore.common.util.filter;

import java.util.Map;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;
import team.creative.creativecore.common.util.registry.exception.RegistryException;

public class FilterRegistry<T extends CompoundSerializer> {
    
    private final NamedTypeRegistry<T> REGISTRY = new NamedTypeRegistry<T>().addConstructorPattern(CompoundTag.class);
    private final Map<String, T> STATIC_ENTRIES = new Object2ObjectArrayMap<>();
    
    public FilterRegistry() {}
    
    public <F extends T> void register(String id, Class<F> clazz, Function<CompoundTag, F> factory) {
        REGISTRY.register(id, clazz);
    }
    
    public <F extends T> void registerStatic(String id, Class<F> clazz, F entry) {
        REGISTRY.register(id, clazz);
        STATIC_ENTRIES.put(id, entry);
    }
    
    public String getId(T filter) throws RegistryException {
        return REGISTRY.getId(filter);
    }
    
    public T read(CompoundTag tag) throws RegistryException {
        String id = tag.getString("t");
        var entry = STATIC_ENTRIES.get(id);
        if (entry != null)
            return entry;
        return REGISTRY.create(id, tag);
    }
    
}
