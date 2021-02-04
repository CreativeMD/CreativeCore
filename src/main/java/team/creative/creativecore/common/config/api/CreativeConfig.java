package team.creative.creativecore.common.config.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import team.creative.creativecore.common.config.sync.ConfigSynchronization;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.TYPE })
public @interface CreativeConfig {
    
    String name() default "";
    
    ConfigSynchronization type() default ConfigSynchronization.UNIVERSAL;
    
    boolean requiresRestart() default false;
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = { ElementType.FIELD })
    @interface IntRange {
        
        public int min();
        
        public int max();
        
        public boolean slider() default true;
        
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = { ElementType.FIELD })
    @interface DecimalRange {
        
        public double min();
        
        public double max();
        
        public boolean slider() default true;
        
    }
    
}
