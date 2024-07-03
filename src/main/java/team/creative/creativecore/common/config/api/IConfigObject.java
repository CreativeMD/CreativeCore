package team.creative.creativecore.common.config.api;

import team.creative.creativecore.Side;

public interface IConfigObject extends ICreativeConfig {
    
    public boolean isDefault(Side side);
    
    public void restoreDefault(Side side, boolean ignoreRestart);
    
}
