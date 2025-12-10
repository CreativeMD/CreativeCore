package team.creative.creativecore.common.config.premade.registry;

import java.util.Objects;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class RegistryObjectConfig<T> implements ICreativeConfig {
    
    public final Registry<T> registry;
    public Identifier identifier;
    public T value;
    
    public RegistryObjectConfig(Registry<T> registry, Identifier identifier) {
        this.registry = registry;
        this.identifier = identifier;
        configured(null);
    }
    
    @Override
    public void configured(Side side) {
        value = registry.getValue(identifier);
    }
    
    public Holder<T> getHolder() {
        return registry.wrapAsHolder(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegistryObjectConfig re)
            return re.registry == registry && Objects.equals(re.identifier, identifier);
        return super.equals(obj);
    }
    
}
