package team.creative.creativecore.common.config.premade.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class RegistryObjectListConfig<T> implements Iterable<T> {
    
    public final Registry<T> registry;
    private final List<RegistryHolder> content = new ArrayList<>();
    
    public RegistryObjectListConfig(Registry<T> registry) {
        this.registry = registry;
    }
    
    public void add(Identifier identifier) {
        if (!contains(identifier))
            content.add(new RegistryHolder(identifier));
    }
    
    @Override
    public Iterator<T> iterator() {
        return new FunctionIterator<>(content, x -> x.get());
    }
    
    public Iterable<Identifier> identifiers() {
        return new FunctionIterator<>(content, x -> x.identifier);
    }
    
    public boolean contains(Identifier location) {
        for (int i = 0; i < content.size(); i++)
            if (content.get(i).identifier.equals(location))
                return true;
        return false;
    }
    
    public Identifier getLocation(int index) {
        return content.get(index).identifier;
    }
    
    public int size() {
        return content.size();
    }
    
    public class RegistryHolder {
        
        public final Identifier identifier;
        private T cache;
        
        public RegistryHolder(Identifier identifier) {
            this.identifier = identifier;
        }
        
        @Override
        public int hashCode() {
            return identifier.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RegistryObjectListConfig.RegistryHolder h)
                return h.identifier.equals(identifier);
            return super.equals(obj);
        }
        
        public T get() {
            if (cache == null)
                cache = registry.getValue(identifier);
            return cache;
            
        }
        
    }
    
}
