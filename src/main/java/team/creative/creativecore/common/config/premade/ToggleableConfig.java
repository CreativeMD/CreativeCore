package team.creative.creativecore.common.config.premade;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class ToggleableConfig<T> implements ICreativeConfig {
    
    public final T value;
    private boolean enabled;
    
    public ToggleableConfig(T value) {
        this.value = value;
    }
    
    public ToggleableConfig(T value, boolean enabled) {
        this.value = value;
        this.enabled = enabled;
    }
    
    @Override
    public void configured(Side side) {
        if (value instanceof ICreativeConfig config)
            config.configured(side);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ToggleableConfig config)
            return config.enabled == enabled && config.value.equals(value);
        return false;
    }
    
}
