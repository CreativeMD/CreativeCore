package com.creativemd.creativecore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

public class CreativeCoreConfig {
    
    @CreativeConfig(name = "use-stencil", type = ConfigSynchronization.CLIENT)
    public boolean useStencil = true;
    
    @CreativeConfig(name = "use-optifine-compat", type = ConfigSynchronization.CLIENT)
    public boolean useOptifineCompat = true;
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    public List<ColorPalette> palette = Arrays.asList(new ColorPalette("basic", ColorUtils.WHITE, ColorUtils.BLACK, ColorUtils.RED, ColorUtils.GREEN, ColorUtils.BLUE));
    
    public static class ColorPalette {
        
        @CreativeConfig
        public String name = "";
        
        @CreativeConfig
        public List<Integer> colors = new ArrayList<>();
        
        public ColorPalette() {
            
        }
        
        public ColorPalette(String name, int... colors) {
            this.name = name;
            for (int i = 0; i < colors.length; i++)
                this.colors.add(colors[i]);
        }
    }
    
}
