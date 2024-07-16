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
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;

public class ConfigTypeArray extends ConfigTypeConveration {
    
    @Override
    public Object readElement(HolderLookup.Provider provider, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        Class clazz = defaultValue.getClass().getComponentType();
        ConfigKey arrayKey = ConfigKey.ofArrayType(key, side);
        
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            int size = Math.min(array.size(), Array.getLength(defaultValue));
            Object object = Array.newInstance(clazz, size);
            for (int i = 0; i < size; i++) {
                arrayKey.read(provider, loadDefault, ignoreRestart, array.get(i), side);
                Array.set(object, i, arrayKey.copy(provider, side));
            }
            return object;
        }
        
        int size = Array.getLength(defaultValue);
        Object object = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            arrayKey.forceValue(Array.get(object, i), side);
            Array.set(object, i, arrayKey.copy(provider, side));
        }
        return object;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, Object value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        int length = Array.getLength(value);
        JsonArray array = new JsonArray();
        ConfigKey arrayKey = ConfigKey.ofArrayType(key, side);
        for (int i = 0; i < length; i++) {
            arrayKey.forceValue(Array.get(value, i), side);
            array.add(arrayKey.write(provider, saveDefault, ignoreRestart, side));
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        parent.add(new GuiListBoxBase<>("data", false, new ArrayList<>()).setDim(50, 150).setExpandable());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Object value, Object defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        ConfigKey arrayKey = ConfigKey.ofArrayType(key, side);
        
        int length = Array.getLength(value);
        List<GuiConfigSubControl> controls = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            var c = arrayKey.create(configParent, "" + i, side);
            arrayKey.forceValue(Array.get(value, i), side);
            arrayKey.load(configParent, c, side);
            controls.add(c);
        }
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Object saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        ConfigKey arrayKey = ConfigKey.ofArrayType(key, side);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        Object value = Array.newInstance(arrayKey.field().getType(), box.size());
        for (int i = 0; i < box.size(); i++) {
            arrayKey.save(box.get(i), configParent, side);
            Array.set(value, i, arrayKey.copy(configParent.provider(), side));
        }
        return value;
    }
    
    @Override
    public Object set(ConfigKey key, Object value) {
        return value;
    }
    
    @Override
    public boolean areEqual(Object one, Object two, ConfigKey key, Side side) {
        int lengthOne = Array.getLength(one);
        int lengthTwo = Array.getLength(two);
        
        ConfigKey arrayKey = ConfigKey.ofArrayType(key, side);
        ConfigTypeConveration converation = arrayKey.converation();
        
        if (lengthOne != lengthTwo)
            return false;
        
        for (int i = 0; i < lengthOne; i++) {
            Object entryOne = Array.get(one, i);
            Object entryTwo = Array.get(two, i);
            
            if (converation != null) {
                arrayKey.forceValue(entryOne, side);
                if (!converation.areEqual(entryOne, entryTwo, arrayKey, side))
                    return false;
                
            } else if (converation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
}
