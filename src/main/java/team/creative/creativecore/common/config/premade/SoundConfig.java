package team.creative.creativecore.common.config.premade;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SoundConfig {
    
    public Identifier event;
    public float volume;
    public float pitch;
    
    public SoundConfig(Identifier location, float volume, float pitch) {
        this.event = location;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    public SoundConfig(Identifier location) {
        this(location, 1, 1);
    }
    
    public void play(Entity entity, SoundSource category) {
        entity.level().playSound(null, entity, BuiltInRegistries.SOUND_EVENT.getValue(event), category, volume, pitch);
    }
    
    public void play(Level level, double x, double y, double z, SoundSource category) {
        level.playSound(null, x, y, z, BuiltInRegistries.SOUND_EVENT.getValue(event), category, volume, pitch);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoundConfig sound)
            return sound.event.equals(event) && sound.volume == volume && sound.pitch == pitch;
        return super.equals(obj);
    }
}
