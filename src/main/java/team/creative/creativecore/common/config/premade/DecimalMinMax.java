package team.creative.creativecore.common.config.premade;

import java.util.Random;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;

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
    public void configured(Dist side) {
        if (min > max) {
            double temp = min;
            this.min = max;
            this.max = temp;
        }
    }
    
    public double spanLength() {
        return max - min;
    }
    
}
