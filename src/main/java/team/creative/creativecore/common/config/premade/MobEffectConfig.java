package team.creative.creativecore.common.config.premade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.IForgeRegistry;
import team.creative.creativecore.common.config.api.CreativeConfig;

public class MobEffectConfig {
    
    @CreativeConfig
    public final RegistryObjectConfig<MobEffect> effect;
    @CreativeConfig
    public int amplifier;
    @CreativeConfig
    public int duration;
    
    public MobEffectConfig(IForgeRegistry<MobEffect> registry, ResourceLocation effect, int amplifier, int duration) {
        this.effect = new RegistryObjectConfig<MobEffect>(registry, effect);
        this.amplifier = amplifier;
        this.duration = duration;
    }
    
    public MobEffectInstance create() {
        return new MobEffectInstance(effect.value, duration, amplifier);
    }
    
}
