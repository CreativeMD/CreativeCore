package net.neoforged.fml.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.neoforged.api.distmarker.Dist;

@Retention(RetentionPolicy.SOURCE)
public @interface Mod {
    
    public String value();
    
    public Dist[] dist() default { Dist.CLIENT, Dist.DEDICATED_SERVER };
    
}
