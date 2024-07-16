package team.creative.creativecore.common.config.converation.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.registry.RegistryObjectConfig;
import team.creative.creativecore.common.gui.GuiParent;

public class ConfigTypeRegistryObject extends ConfigTypeConveration<RegistryObjectConfig> {
    
    @Override
    public RegistryObjectConfig readElement(HolderLookup.Provider provider, RegistryObjectConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return new RegistryObjectConfig(defaultValue.registry, new ResourceLocation(element.getAsString()));
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, RegistryObjectConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        return new JsonPrimitive(value.location.toString());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        RegistryObjectConfig value = (RegistryObjectConfig) key.get();
        GuiRegistryObjectHandler.REGISTRY.get(value.registry).createControls(parent, value.registry);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(RegistryObjectConfig value, RegistryObjectConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiRegistryObjectHandler.REGISTRY.get(value.registry).loadValue(parent, value.registry, value.location);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected RegistryObjectConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        RegistryObjectConfig value = (RegistryObjectConfig) key.get();
        return new RegistryObjectConfig<>(value.registry, GuiRegistryObjectHandler.REGISTRY.get(value.registry).saveValue(parent, value.registry));
    }
    
    @Override
    public RegistryObjectConfig set(ConfigKey key, RegistryObjectConfig value) {
        return value;
    }
    
}
