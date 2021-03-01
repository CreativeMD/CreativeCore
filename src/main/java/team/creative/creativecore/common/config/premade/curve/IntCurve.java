package team.creative.creativecore.common.config.premade.curve;

import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class IntCurve implements ICreativeConfig, Curve {
    
    @CreativeConfig
    public int min;
    @CreativeConfig
    public int minValue;
    
    @CreativeConfig
    public int max;
    @CreativeConfig
    public int maxValue;
    
    public IntCurve(int min, int minValue, int max, int maxValue) {
        this.min = min;
        this.minValue = minValue;
        this.max = max;
        this.maxValue = maxValue;
    }
    
    @Override
    public double valueAt(double x) {
        if (x <= min)
            return minValue;
        
        if (x >= max)
            return maxValue;
        
        double percent = (x - min) / (max - min);
        return (maxValue - minValue) * percent + minValue;
    }
    
    @Override
    public void configured() {
        if (min > max) {
            int temp = min;
            this.min = max;
            this.max = temp;
            
            temp = minValue;
            this.minValue = maxValue;
            this.maxValue = temp;
        }
    }
}
