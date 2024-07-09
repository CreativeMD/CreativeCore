package team.creative.creativecore.common.config.premade.registry;

import java.util.Objects;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

public class RegistryTagConfig<T> {
    
    public final Registry<T> registry;
    public TagKey<T> tag;
    
    public RegistryTagConfig(Registry<T> registry, TagKey<T> tag) {
        this.registry = registry;
        this.tag = tag;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegistryTagConfig re)
            return re.registry == registry && Objects.equals(re.tag, tag);
        return super.equals(obj);
    }
    
}
