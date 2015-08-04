package com.n247s.api.eventapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomEventSubscribe
{
	public Priority eventPriority() default Priority.Normal;
	
	public enum Priority
	{
		Highest,
		High,
		Normal,
		Low,
		Lowest;
		
		/**
		 * @param i - The number of Priority you want to get.
		 * @return<br>
		 * 0 - High<br>
		 * 1 - Intermediate<br>
		 * 2 - Normal<br>
		 * 3 - Low<br>
		 * 4 - Lowest<br>
		 */
		public static Priority getPriorityInOrder(int i)
		{
			switch(i)
			{
				case 0: return Highest;
				case 1: return High;
				case 2: return Normal;
				case 3: return Low;
				case 4: return Lowest;
				default: EventApi.logger.catching(new IllegalArgumentException("Priority Number Can't be lower than 0, or higher than 4!"));
			}
			return null;
		}
	}
}
