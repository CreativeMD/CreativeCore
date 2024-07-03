package team.creative.creativecore.common.config.key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.api.IConfigObject;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;

public class ConfigKeyCacheType extends ConfigKeyCache {
    
    public final ConfigTypeConveration converation;
    public final Class clazz;
    
    protected Object value;
    protected Object defaultValue;
    
    public ConfigKeyCacheType(Type fieldType, Class clazz) {
        super(fieldType);
        this.clazz = clazz;
        this.converation = ConfigTypeConveration.getUnsafe(clazz);
    }
    
    @Override
    public void set(Object value, Side side) {
        this.value = value;
    }
    
    @Override
    public void read(HolderLookup.Provider provider, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side) {
        if (defaultValue == null || defaultValue instanceof IConfigObject) // An IConfigObject needs to be generated new every time because it will be returned as a value
            defaultValue = ConfigTypeConveration.createObject(this);
        value = converation.readElement(provider, defaultValue, loadDefault, ignoreRestart, element, side, this);
    }
    
    @Override
    public JsonElement write(HolderLookup.Provider provider, boolean saveDefault, boolean ignoreRestart, Side side) {
        return converation.writeElement(provider, get(), saveDefault, ignoreRestart, side, this);
    }
    
    @Override
    public Object copy(HolderLookup.Provider provider, Side side) {
        return converation.readElement(provider, get(), false, true, write(provider, false, true, side), side, this);
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }
    
    @Override
    public Class getType() {
        return clazz;
    }
    
    @Override
    public Type getGenericType() {
        return fieldType;
    }
    
    @Override
    public Object get() {
        return value;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public GuiConfigSubControl create(IGuiConfigParent configParent, String name) {
        var control = new GuiConfigSubControl(name);
        converation.createControls(control, configParent, this);
        return control;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public void load(IGuiConfigParent configParent, GuiConfigSubControl control) {
        converation.loadValue(value, defaultValue, control, configParent, this);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void save(GuiConfigSubControl control, IGuiConfigParent configParent) {
        set(converation.save(control, null, this), Side.SERVER);
    }
}
