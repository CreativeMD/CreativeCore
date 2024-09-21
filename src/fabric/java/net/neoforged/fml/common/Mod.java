package net.neoforged.fml.common;

import net.neoforged.api.distmarker.Dist;

public @interface Mod {
    
    public String value();
    
    public Dist[] dist() default { Dist.CLIENT, Dist.DEDICATED_SERVER };
    
}
