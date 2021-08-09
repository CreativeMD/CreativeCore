package team.creative.creativecore.common.config.premade.curve;

import java.util.Random;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.premade.DecimalMinMax;
import team.creative.creativecore.common.config.premade.IntMinMax;

public class DecimalCurve implements ICreativeConfig, Curve {
    
    @CreativeConfig
    public double min;
    @CreativeConfig
    public double minValue;
    
    @CreativeConfig
    public double max;
    @CreativeConfig
    public double maxValue;
    
    public DecimalCurve(double min, double minValue, double max, double maxValue) {
        this.min = min;
        this.minValue = minValue;
        this.max = max;
        this.maxValue = maxValue;
    }
    
    public DecimalCurve(Random rand, IntMinMax duration, DecimalMinMax intensity) {
        this.min = 0;
        this.minValue = intensity.next(rand);
        this.max = duration.next(rand);
        this.maxValue = 0;
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
    public void configured(Dist side) {
        if (min > max) {
            double temp = min;
            this.min = max;
            this.max = temp;
            
            temp = minValue;
            this.minValue = maxValue;
            this.maxValue = temp;
        }
    }
}
