package com.creativemd.creativecore.common.config.converation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeArray extends ConfigTypeConveration {
    
    @Override
    public Object readElement(Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            int size = Math.min(array.size(), Array.getLength(defaultValue));
            Object object = Array.newInstance(defaultValue.getClass().getComponentType(), size);
            for (int i = 0; i < size; i++)
                Array.set(object, i, read(defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, ignoreRestart, array.get(i), side, null));
            return object;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        int length = Array.getLength(value);
        JsonArray array = new JsonArray();
        for (int i = 0; i < length; i++)
            array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, ignoreRestart, side, null));
        return array;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
        parent.height = 160;
        GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.width - 10, 150, false, new ArrayList<>());
        parent.addControl(listBox);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void loadValue(Object value, GuiParent parent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = value.getClass().getComponentType();
        ConfigTypeConveration converation = get(clazz);
        
        int length = Array.getLength(value);
        List<GuiConfigSubControl> controls = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object entry = Array.get(value, i);
            GuiConfigSubControl control = new GuiConfigSubControl("" + i, 2, 0, parent.width, 14);
            converation.createControls(control, null, clazz, Math.min(100, control.width));
            converation.loadValue(entry, control, null);
            controls.add(control);
        }
        
        box.addAll(controls);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    protected Object saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = clazz.getComponentType();
        ConfigTypeConveration converation = get(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        Object value = Array.newInstance(subClass, box.size());
        for (int i = 0; i < box.size(); i++)
            Array.set(value, i, converation.save(box.get(i), subClass, null));
        return value;
    }
    
    @Override
    public Object set(ConfigKeyField key, Object value) {
        return value;
    }
    
    @Override
    public boolean areEqual(Object one, Object two) {
        int lengthOne = Array.getLength(one);
        int lengthTwo = Array.getLength(two);
        
        if (lengthOne != lengthTwo)
            return false;
        
        for (int i = 0; i < lengthOne; i++) {
            Object entryOne = Array.get(one, i);
            Object entryTwo = Array.get(two, i);
            
            if (entryOne.getClass().isArray()) {
                if (!entryTwo.getClass().isArray())
                    return false;
                
                if (!areEqual(entryOne, entryTwo))
                    return false;
            }
            
            if (!entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
    
    @Override
    public Object createPrimitiveDefault(Class clazz) {
        return null;
    }
    
}
