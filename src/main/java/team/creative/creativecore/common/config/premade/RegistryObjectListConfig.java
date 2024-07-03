package team.creative.creativecore.common.config.premade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class RegistryObjectListConfig<T> implements ICreativeConfig, Iterable<T> {
    
    public final Registry<T> registry;
    private final List<RegistryHolder> content = new ArrayList<>();
    
    public RegistryObjectListConfig(Registry<T> registry) {
        this.registry = registry;
        configured(null);
    }
    
    public void add(ResourceLocation location) {
        if (!contains(location))
            content.add(new RegistryHolder(location));
    }
    
    @Override
    public void configured(Side side) {}
    
    @Override
    public Iterator<T> iterator() {
        return new FunctionIterator<>(content, x -> x.get());
    }
    
    public Iterable<ResourceLocation> locations() {
        return new FunctionIterator<>(content, x -> x.location);
    }
    
    public boolean contains(ResourceLocation location) {
        for (int i = 0; i < content.size(); i++)
            if (content.get(i).location.equals(location))
                return true;
        return false;
    }
    
    public ResourceLocation getLocation(int index) {
        return content.get(index).location;
    }
    
    public int size() {
        return content.size();
    }
    
    public class RegistryHolder {
        
        public final ResourceLocation location;
        private T cache;
        
        public RegistryHolder(ResourceLocation location) {
            this.location = location;
        }
        
        @Override
        public int hashCode() {
            return location.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RegistryObjectListConfig.RegistryHolder h)
                return h.location.equals(location);
            return super.equals(obj);
        }
        
        public T get() {
            if (cache == null)
                cache = registry.get(location);
            return cache;
            
        }
        
    }
    
}
