package team.creative.creativecore.common.config.premade;

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
    }
    
    @Override
    public void configured(Side side) {
        value = registry.get(location);
    }
    
}
