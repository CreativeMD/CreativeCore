package team.creative.creativecore.common.config.converation.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.registry.RegistryTagConfig;
import team.creative.creativecore.common.gui.GuiParent;

public class ConfigTypeRegistryTag extends ConfigTypeConveration<RegistryTagConfig> {
    
    @Override
    public RegistryTagConfig readElement(HolderLookup.Provider provider, RegistryTagConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return new RegistryTagConfig(defaultValue.registry, TagKey.create(defaultValue.registry.key(), new ResourceLocation(element.getAsString())));
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, RegistryTagConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        return new JsonPrimitive(value.tag.location().toString());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        RegistryTagConfig value = (RegistryTagConfig) key.get();
        GuiRegistryTagHandler.REGISTRY.get(value.registry).createControls(parent, value.registry);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(RegistryTagConfig value, RegistryTagConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiRegistryTagHandler.REGISTRY.get(value.registry).loadValue(parent, value.registry, value.tag);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected RegistryTagConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        RegistryTagConfig value = (RegistryTagConfig) key.get();
        return new RegistryTagConfig<>(value.registry, GuiRegistryTagHandler.REGISTRY.get(value.registry).saveValue(parent, value.registry));
    }
    
    @Override
    public RegistryTagConfig set(ConfigKey key, RegistryTagConfig value) {
        return value;
    }
    
}
