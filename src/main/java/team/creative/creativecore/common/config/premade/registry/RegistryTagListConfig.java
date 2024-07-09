package team.creative.creativecore.common.config.premade.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

public class RegistryTagListConfig<T> implements Iterable<TagKey<T>> {
    
    public final Registry<T> registry;
    private final List<TagKey<T>> content = new ArrayList<>();
    
    public RegistryTagListConfig(Registry<T> registry) {
        this.registry = registry;
    }
    
    public void add(TagKey tag) {
        if (!content.contains(tag))
            content.add(tag);
    }
    
    @Override
    public Iterator<TagKey<T>> iterator() {
        return content.iterator();
    }
    
    public TagKey<T> get(int index) {
        return content.get(index);
    }
    
    public int size() {
        return content.size();
    }
    
}
