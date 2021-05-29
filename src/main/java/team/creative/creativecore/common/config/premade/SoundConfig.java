package team.creative.creativecore.common.config.premade;

import net.minecraft.util.ResourceLocation;

public class SoundConfig {
    
    public ResourceLocation event;
    public float volume;
    public float pitch;
    
    public SoundConfig(ResourceLocation location, float volume, float pitch) {
        this.event = location;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    public SoundConfig(ResourceLocation location) {
        this(location, 1, 1);
    }
    
}
