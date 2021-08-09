package com.creativemd.creativecore.common.config.api;

import net.minecraftforge.fml.relauncher.Side;

public interface ICreativeConfig {
    
    public void configured();
    
    public default void configured(Side side) {
        configured();
    }
    
}
