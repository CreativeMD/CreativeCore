package team.creative.creativecore.common.config.premade;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
    
    public SoundEvent getEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(event);
    }
    
    public void play(Entity entity, SoundSource category) {
        var event = getEvent();
        if (event != null)
            entity.level().playSound(null, entity, event, category, volume, pitch);
    }
    
    public void play(Level level, double x, double y, double z, SoundSource category) {
        var event = getEvent();
        if (event != null)
            level.playSound(null, x, y, z, event, category, volume, pitch);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoundConfig sound)
            return sound.event.equals(event) && sound.volume == volume && sound.pitch == pitch;
        return super.equals(obj);
    }
}
