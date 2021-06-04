package team.creative.creativecore.common.config.premade;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

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
    
    public void play(Entity entity, SoundCategory category) {
        entity.level.playSound(null, entity, ForgeRegistries.SOUND_EVENTS.getValue(event), category, volume, pitch);
    }
    
    public void play(World world, double x, double y, double z, SoundCategory category) {
        world.playSound(null, x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(event), category, volume, pitch);
    }
}
