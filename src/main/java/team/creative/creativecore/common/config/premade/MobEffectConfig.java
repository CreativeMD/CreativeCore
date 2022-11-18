package team.creative.creativecore.common.config.premade;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import team.creative.creativecore.common.config.api.CreativeConfig;

public class MobEffectConfig {
    
    @CreativeConfig
    public RegistryObjectConfig<MobEffect> effect;
    @CreativeConfig
    public int amplifier;
    @CreativeConfig
    public int duration;
    
    public MobEffectConfig(Registry<MobEffect> registry, ResourceLocation effect, int amplifier, int duration) {
        this.effect = new RegistryObjectConfig<MobEffect>(registry, effect);
        this.amplifier = amplifier;
        this.duration = duration;
    }
    
    public MobEffectInstance create() {
        return new MobEffectInstance(effect.value, duration, amplifier);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobEffectConfig mob)
            return mob.effect.equals(effect) && mob.amplifier == amplifier && mob.duration == duration;
        return super.equals(obj);
    }
    
}
