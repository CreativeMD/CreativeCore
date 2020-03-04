package com.creativemd.creativecore.common.config.premade;

import java.util.Random;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;

public class IntMinMax implements ICreativeConfig {
	
	@CreativeConfig
	public int min;
	@CreativeConfig
	public int max;
	
	public IntMinMax(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	public int next(Random rand) {
		if (min == max)
			return min;
		return min + rand.nextInt(max - min);
	}
	
	@Override
	public void configured() {
		if (min > max) {
			int temp = min;
			this.min = max;
			this.max = temp;
		}
	}
	
}
