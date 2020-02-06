package team.creative.creativecore.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface CreativeConfig {
	
	String name() default "";
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.FIELD })
	public @interface IntRange {
		
		public int min();
		
		public int max();
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.FIELD })
	public @interface DoubleRange {
		
		public double min();
		
		public double max();
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = { ElementType.FIELD })
	public @interface FloatRange {
		
		public float min();
		
		public float max();
		
	}
	
}
