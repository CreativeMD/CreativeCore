package team.creative.creativecore.common.config.converation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

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
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;

public class ConfigTypeArray extends ConfigTypeConveration {
    
    @Override
    public Object readElement(HolderLookup.Provider provider, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        Class clazz = defaultValue.getClass().getComponentType();
        
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            int size = Math.min(array.size(), Array.getLength(defaultValue));
            Object object = Array.newInstance(clazz, size);
            for (int i = 0; i < size; i++)
                Array.set(object, i, read(provider, defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, ignoreRestart, array.get(i), side, null));
            return object;
        }
        
        int size = Array.getLength(defaultValue);
        
        Object object = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++)
            Array.set(object, i, copy(provider, side, Array.get(object, i), clazz));
        return object;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        int length = Array.getLength(value);
        JsonArray array = new JsonArray();
        for (int i = 0; i < length; i++)
            array.add(write(provider, value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, ignoreRestart, side, null));
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
        parent.add(new GuiListBoxBase<>("data", false, new ArrayList<>()).setDim(50, 150).setExpandable());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Object value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = value.getClass().getComponentType();
        ConfigTypeConveration converation = get(clazz);
        
        int length = Array.getLength(value);
        List<GuiConfigSubControl> controls = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object entry = Array.get(value, i);
            GuiConfigSubControl control = new GuiConfigSubControl("" + i);
            converation.createControls(control, null, null, clazz);
            converation.loadValue(entry, control, null, null);
            controls.add(control);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Object saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = clazz.getComponentType();
        ConfigTypeConveration converation = get(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        Object value = Array.newInstance(subClass, box.size());
        for (int i = 0; i < box.size(); i++)
            Array.set(value, i, converation.save(box.get(i), null, subClass, null));
        return value;
    }
    
    @Override
    public Object set(ConfigKeyField key, Object value) {
        return value;
    }
    
    @Override
    public boolean areEqual(Object one, Object two, @Nullable ConfigKeyField key) {
        int lengthOne = Array.getLength(one);
        int lengthTwo = Array.getLength(two);
        
        Class clazz = key.getDefault().getClass().getComponentType();
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (lengthOne != lengthTwo)
            return false;
        
        for (int i = 0; i < lengthOne; i++) {
            Object entryOne = Array.get(one, i);
            Object entryTwo = Array.get(two, i);
            
            if (conversation != null && !conversation.areEqual(entryOne, entryTwo, null))
                return false;
            
            if (conversation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
}
