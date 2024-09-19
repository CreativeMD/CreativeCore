package team.creative.creativecore.common.config.premade.registry;

import java.util.Objects;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class RegistryObjectConfig<T> implements ICreativeConfig {
    
    public final Registry<T> registry;
    public ResourceLocation location;
    public T value;
    
    public RegistryObjectConfig(Registry<T> registry, ResourceLocation location) {
        this.registry = registry;
        this.location = location;
        configured(null);
    }
    
    @Override
    public void configured(Side side) {
        value = registry.get(location);
    }
    
    public Holder<T> getHolder() {
        return registry.wrapAsHolder(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegistryObjectConfig re)
            return re.registry == registry && Objects.equals(re.location, location);
        return super.equals(obj);
    }
    
}
