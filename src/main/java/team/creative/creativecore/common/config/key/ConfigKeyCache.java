package team.creative.creativecore.common.config.key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;

public abstract class ConfigKeyCache extends ConfigKey {
    
    public static ConfigKeyCache of(Type type) {
        Class clazz = type instanceof ParameterizedType p ? (Class) p.getRawType() : (Class) type;
        if (ConfigTypeConveration.has(clazz))
            return new ConfigKeyCacheType(type, clazz);
        return new ConfigKeyCacheHolder(type);
    }
    
    public static ConfigKeyCache ofGenericType(ConfigKey key) {
        return of(ConfigTypeConveration.getGenericType(key));
    }
    
    public final Type fieldType;
    
    public ConfigKeyCache(Type fieldType) {
        this.fieldType = fieldType;
    }
    
    public abstract void set(Object value, Side side);
    
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public abstract GuiConfigSubControl create(IGuiConfigParent configParent, String name);
    
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public abstract void load(IGuiConfigParent configParent, GuiConfigSubControl control);
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public abstract void save(GuiConfigSubControl control, IGuiConfigParent configParent);
    
}
