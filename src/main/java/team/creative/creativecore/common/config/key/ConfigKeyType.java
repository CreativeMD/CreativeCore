package team.creative.creativecore.common.config.key;

import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.api.ICreativeConfig;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.field.ConfigField;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;

public class ConfigKeyType extends ConfigKey {
    
    public final ConfigTypeConveration converation;
    public final Object defaultValue;
    
    public ConfigKeyType(ConfigField field, String name, Object defaultValue, ConfigSynchronization synchronization, boolean requiresRestart) {
        super(field, name, synchronization, requiresRestart);
        this.converation = ConfigTypeConveration.get(field.getType());
        this.defaultValue = defaultValue;
    }
    
    private void set(Object object, Side side) {
        if (!(defaultValue instanceof IConfigObject))
            field.set(converation.set(this, object));
    }
    
    @Override
    protected boolean checkEqual(Object one, Object two, Side side) {
        return converation.areEqual(one, two, this, side);
    }
    
    @Override
    public boolean isDefault(Side side) {
        if (defaultValue instanceof IConfigObject c)
            return c.isDefault(side);
        return checkEqual(defaultValue, get(), side);
    }
    
    @Override
    public boolean isDefault(Object value, Side side) {
        if (defaultValue instanceof IConfigObject c)
            return c.isDefault(side);
        return checkEqual(defaultValue, value, side);
    }
    
    @Override
    public void restoreDefault(Side side, boolean ignoreRestart) {
        if (defaultValue instanceof IConfigObject c)
            c.restoreDefault(side, ignoreRestart);
        else
            set(defaultValue, side);
    }
    
    @Override
    public void read(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        set(converation.readElement(provider, defaultValue, loadDefault, ignoreRestart, element, side, this), side);
    }
    
    @Override
    public JsonElement write(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
        return converation.writeElement(provider, get(), saveDefault, ignoreRestart, side, this);
    }
    
    @Override
    public Object copy(HolderLookup.Provider provider, Side side) {
        return converation.readElement(provider, defaultValue, false, true, write(provider, false, true, side), side, this);
    }
    
    @Override
    public boolean isFolder() {
        return false;
    }
    
    @Override
    public ICreativeConfigHolder holder() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void triggerConfigured(Side side) {
        if (get() instanceof ICreativeConfig c)
            c.configured(side);
    }
    
    @Override
    public void forceValue(Object object, Side side) {
        field.set(object);
    }
    
    @Override
    public ConfigTypeConveration converation() {
        return converation;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public GuiConfigSubControl create(IGuiConfigParent configParent, String name, Side side) {
        var control = new GuiConfigSubControl(name);
        converation.createControls(control, configParent, this, side);
        return control;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public void load(IGuiConfigParent configParent, GuiConfigSubControl control, Side side) {
        converation.loadValue(get(), defaultValue, control, configParent, this, side);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void save(GuiConfigSubControl control, IGuiConfigParent configParent, Side side) {
        set(converation.save(control, configParent, this, side), side);
    }
    
}