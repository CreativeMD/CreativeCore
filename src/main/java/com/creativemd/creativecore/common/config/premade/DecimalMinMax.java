package com.creativemd.creativecore.common.config.premade;

import java.util.Random;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;

public class DecimalMinMax implements ICreativeConfig {
	
	@CreativeConfig
	public double min;
	@CreativeConfig
	public double max;
	
	public DecimalMinMax(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public double next(Random rand) {
		if (min == max)
			return min;
		return min + rand.nextDouble() * (max - min);
	}
	
	@Override
	public void configured() {
		if (min > max) {
			double temp = min;
			this.min = max;
			this.max = temp;
		}
	}
	
}
