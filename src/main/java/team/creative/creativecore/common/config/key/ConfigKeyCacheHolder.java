package team.creative.creativecore.common.config.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;

public class ConfigKeyCacheHolder extends ConfigKeyCache {
    
    private ICreativeConfigHolder holder;
    private Object value;
    
    public ConfigKeyCacheHolder(Type fieldType) {
        super(fieldType);
    }
    
    public ICreativeConfigHolder holder() {
        return holder;
    }
    
    @Override
    public void set(Object value, Side side) {
        this.holder = ConfigHolderObject.createUnrelated(side, value, get());
        this.value = value;
    }
    
    @Override
    public Object get() {
        return value;
    }
    
    @Override
    public Class getType() {
        return value.getClass();
    }
    
    @Override
    public Type getGenericType() {
        return value.getClass();
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }
    
    @Override
    public Object copy(Provider provider, Side side) {
        Object value = ConfigTypeConveration.createObject(this);
        JsonElement element = write(provider, false, true, side);
        ICreativeConfigHolder.read(provider, ConfigHolderObject.createUnrelated(side, value, get()), true, true, element, side);
        return value;
    }
    
    @Override
    public void read(Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        ICreativeConfigHolder.read(provider, holder, loadDefault, ignoreRestart, element, side);
    }
    
    @Override
    public JsonElement write(Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
        return ICreativeConfigHolder.write(provider, holder, saveDefault, ignoreRestart, side);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public GuiConfigSubControl create(IGuiConfigParent configParent, String name) {
        return new GuiConfigSubControlHolder(name, null, null, configParent::changed);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public void load(IGuiConfigParent configParent, GuiConfigSubControl control) {
        GuiConfigSubControlHolder c = (GuiConfigSubControlHolder) control;
        c.load(holder, value);
        c.createControls();
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void save(GuiConfigSubControl control, IGuiConfigParent configParent) {
        GuiConfigSubControlHolder c = (GuiConfigSubControlHolder) control;
        c.save();
        value = c.value;
    }
    
}
