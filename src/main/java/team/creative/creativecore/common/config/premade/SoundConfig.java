package team.creative.creativecore.common.config.premade;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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
    
    public void play(Entity entity, SoundSource category) {
        entity.level.playSound(null, entity, Registry.SOUND_EVENT.get(event), category, volume, pitch);
    }
    
    public void play(Level level, double x, double y, double z, SoundSource category) {
        level.playSound(null, x, y, z, Registry.SOUND_EVENT.get(event), category, volume, pitch);
    }
}
