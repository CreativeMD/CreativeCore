package team.creative.creativecore.common.config.premade;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class RegistryObjectConfig<T extends IForgeRegistryEntry<T>> implements ICreativeConfig {
    
    public final IForgeRegistry<T> registry;
    public ResourceLocation location;
    public T value;
    
    public RegistryObjectConfig(IForgeRegistry<T> registry, ResourceLocation location) {
        this.registry = registry;
        this.location = location;
    }
    
    @Override
    public void configured(Side side) {
        value = registry.getValue(location);
    }
    
}
