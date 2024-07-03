package team.creative.creativecore.common.config.converation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyCache;
import team.creative.creativecore.common.config.key.ConfigKeyCacheType;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;

public class ConfigTypeArray extends ConfigTypeConveration {
    
    @Override
    public Object readElement(HolderLookup.Provider provider, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        Class clazz = defaultValue.getClass().getComponentType();
        ConfigKeyCache arrayKey = ConfigKeyCache.of(clazz);
        
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            int size = Math.min(array.size(), Array.getLength(defaultValue));
            Object object = Array.newInstance(clazz, size);
            for (int i = 0; i < size; i++) {
                arrayKey.read(provider, loadDefault, ignoreRestart, array.get(i), side);
                Array.set(object, i, arrayKey.get());
            }
            return object;
        }
        
        int size = Array.getLength(defaultValue);
        Object object = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            arrayKey.set(Array.get(object, i), side);
            Array.set(object, i, arrayKey.copy(provider, side));
        }
        return object;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, Object value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        int length = Array.getLength(value);
        JsonArray array = new JsonArray();
        Class clazz = value.getClass().getComponentType();
        ConfigKeyCache arrayKey = ConfigKeyCache.of(clazz);
        for (int i = 0; i < length; i++) {
            arrayKey.set(Array.get(value, i), side);
            array.add(arrayKey.write(provider, saveDefault, ignoreRestart, side));
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        parent.add(new GuiListBoxBase<>("data", false, new ArrayList<>()).setDim(50, 150).setExpandable());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Object value, Object defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = value.getClass().getComponentType();
        ConfigKeyCache arrayKey = ConfigKeyCache.of(clazz);
        
        int length = Array.getLength(value);
        List<GuiConfigSubControl> controls = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            var c = arrayKey.create(configParent, "" + i);
            arrayKey.set(Array.get(value, i), Side.SERVER);
            arrayKey.load(configParent, c);
            controls.add(c);
        }
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Object saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        Class clazz = key.getType().getComponentType();
        ConfigKeyCache arrayKey = ConfigKeyCache.of(clazz);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        Object value = Array.newInstance(clazz, box.size());
        for (int i = 0; i < box.size(); i++) {
            arrayKey.save(box.get(i), configParent);
            Array.set(value, i, arrayKey.get());
        }
        return value;
    }
    
    @Override
    public Object set(ConfigKey key, Object value) {
        return value;
    }
    
    @Override
    public boolean areEqual(Object one, Object two, ConfigKey key) {
        int lengthOne = Array.getLength(one);
        int lengthTwo = Array.getLength(two);
        
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        ConfigTypeConveration converation = listKey instanceof ConfigKeyCacheType t ? t.converation : null;
        
        if (lengthOne != lengthTwo)
            return false;
        
        for (int i = 0; i < lengthOne; i++) {
            Object entryOne = Array.get(one, i);
            Object entryTwo = Array.get(two, i);
            
            if (converation != null && !converation.areEqual(entryOne, entryTwo, null))
                return false;
            
            if (converation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
}
