package team.creative.creativecore.common.config.converation;

import java.lang.reflect.ParameterizedType;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.premade.ToggleableConfig;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiCheckBox;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeToggleable extends ConfigTypeConveration<ToggleableConfig> {
    
    public Class getConfigType(ConfigKeyField key) {
        ParameterizedType type = (ParameterizedType) key.field.getGenericType();
        return (Class) type.getActualTypeArguments()[0];
    }
    
    @Override
    public ToggleableConfig readElement(HolderLookup.Provider provider, ToggleableConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        Class clazz = getConfigType(key);
        if (element.isJsonObject()) {
            Object value;
            JsonObject object = element.getAsJsonObject();
            ConfigTypeConveration conversation = getUnsafe(clazz);
            if (conversation != null)
                value = conversation.readElement(provider, ConfigTypeConveration.createObject(clazz), loadDefault, ignoreRestart, object.get("content"), side, null);
            else {
                value = ConfigTypeConveration.createObject(clazz);
                holderConveration.readElement(provider, ConfigHolderObject.createUnrelated(side, value, value), loadDefault, ignoreRestart, object.get("content"), side, null);
            }
            return new ToggleableConfig(value, object.get("enabled").getAsBoolean());
        }
        return new ToggleableConfig(defaultValue.value, defaultValue.isEnabled());
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, ToggleableConfig value, ToggleableConfig defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        Class clazz = getConfigType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        JsonObject object = new JsonObject();
        object.addProperty("enabled", value.isEnabled());
        if (conversation != null)
            object.add("content", conversation.writeElement(provider, value.value, null, true, ignoreRestart, side, null));
        else
            object.add("content", holderConveration.writeElement(provider, ConfigHolderObject.createUnrelated(side, value.value, value.value), null, true, ignoreRestart, side,
                null));
        return object;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
        parent.flow = GuiFlow.STACK_Y;
        parent.add(new GuiCheckBox("enabled", true).setTranslate("gui.config.enabled"));
        Class subClass = getConfigType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiConfigSubControl control;
        if (converation != null) {
            control = new GuiConfigSubControl("content");
            converation.createControls(control, null, null, subClass);
        } else {
            control = new GuiConfigSubControlHolder("content", null, null, configParent::changed);
            ((GuiConfigSubControlHolder) control).createControls();
        }
        parent.add(control);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(ToggleableConfig value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        parent.get("enabled", GuiCheckBox.class).value = value.isEnabled();
        
        Class clazz = getConfigType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        GuiConfigSubControl control = parent.get("content");
        if (converation != null)
            converation.loadValue(value.value, control, null, null);
        else {
            Object entry = copy(configParent.provider(), Side.SERVER, value.value, clazz);
            ((GuiConfigSubControlHolder) control).load(ConfigHolderObject.createUnrelated(Side.SERVER, entry, entry), entry);
            ((GuiConfigSubControlHolder) control).createControls();
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected ToggleableConfig saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = getConfigType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiConfigSubControl control = parent.get("content");
        Object value;
        if (converation != null)
            value = converation.save(control, null, subClass, null);
        else {
            ((GuiConfigSubControlHolder) control).save();
            value = ((GuiConfigSubControlHolder) control).value;
        }
        return new ToggleableConfig(value, parent.get("enabled", GuiCheckBox.class).value);
    }
    
    @Override
    public ToggleableConfig set(ConfigKeyField key, ToggleableConfig value) {
        return value;
    }
    
    @Override
    public boolean areEqual(ToggleableConfig one, ToggleableConfig two, @Nullable ConfigKeyField key) {
        if (one.isEnabled() != two.isEnabled())
            return false;
        
        Class clazz = getConfigType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (conversation != null && !conversation.areEqual(one.value, two.value, null))
            return false;
        
        return conversation != null || one.value.equals(two.value);
    }
}
