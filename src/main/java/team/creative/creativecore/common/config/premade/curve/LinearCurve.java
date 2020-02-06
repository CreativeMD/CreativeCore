package team.creative.creativecore.common.config.premade.curve;

import team.creative.creativecore.common.config.CreativeConfig;
import team.creative.creativecore.common.config.ICreativeConfig;

public class LinearCurve implements ICreativeConfig, Curve {
	
	@CreativeConfig
	public double value;
	
	public LinearCurve(double value) {
		this.value = value;
	}
	
	@Override
	public double valueAt(double x) {
		return value;
	}
	
	@Override
	public void configured() {
		
	}
	
}
