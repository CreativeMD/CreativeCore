package com.creativemd.creativecore.common.config.converation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControlHolder;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeList extends ConfigTypeConveration<List> {
    
    private ConfigHolderObject constructHolder(Side side, Object value) {
        return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
    }
    
    private Object constructEmpty(Class clazz) {
        try {
            Constructor con = clazz.getConstructor();
            return con.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List readElement(List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            Class clazz = getListType(key);
            List list = new ArrayList<>(array.size());
            ConfigTypeConveration conversation = getUnsafe(clazz);
            for (int i = 0; i < array.size(); i++)
                if (conversation != null)
                    list.add(conversation.readElement(ConfigTypeConveration.createObject(clazz), loadDefault, ignoreRestart, array.get(i), side, null));
                else {
                    Object value = constructEmpty(clazz);
                    holderConveration
                        .readElement(constructHolder(side, value), loadDefault, ignoreRestart, array.get(i), side, null);
                    list.add(value);
                }
            return list;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        JsonArray array = new JsonArray();
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        for (int i = 0; i < value.size(); i++)
            if (conversation != null)
                array.add(conversation.writeElement(value.get(i), null, saveDefault, ignoreRestart, side, key));
            else
                array.add(holderConveration.writeElement(constructHolder(side, value.get(i)), null, saveDefault, ignoreRestart, side, key));
        return array;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
        parent.height = 160;
        GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.width - 10, 130, true, new ArrayList<>());
        parent.addControl(listBox);
        
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        int parentWidth = parent.width - 10;
        
        parent.addControl(new GuiButton("add", 0, 140) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                GuiConfigSubControl control;
                if (converation != null) {
                    control = new GuiConfigSubControl("" + 0, 2, 0, parentWidth, 14);
                    converation.createControls(control, null, subClass, Math.max(100, control.width - 35));
                } else {
                    Object value = constructEmpty(subClass);
                    ConfigHolderObject holder = constructHolder(Side.SERVER, value);
                    control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, holder, value);
                    ((GuiConfigSubControlHolder) control).createControls();
                }
                listBox.add(control);
            }
        });
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void loadValue(List value, GuiParent parent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = getListType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        int parentWidth = parent.width - 10;
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        for (int i = 0; i < value.size(); i++) {
            Object entry = value.get(i);
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
                converation.createControls(control, null, clazz, Math.max(100, control.width - 35));
                converation.loadValue(entry, control, null);
            } else {
                control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, constructHolder(Side.SERVER, entry), entry);
                ((GuiConfigSubControlHolder) control).createControls();
            }
            controls.add(control);
        }
        
        box.addAll(controls);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    protected List saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        List value = new ArrayList(box.size());
        for (int i = 0; i < box.size(); i++)
            if (converation != null)
                value.add(converation.save(box.get(i), subClass, null));
            else {
                ((GuiConfigSubControlHolder) box.get(i)).save();
                value.add(((GuiConfigSubControlHolder) box.get(i)).value);
            }
        return value;
    }
    
    @Override
    public List set(ConfigKeyField key, List value) {
        return value;
    }
    
    public Class getListType(ConfigKeyField key) {
        ParameterizedType type = (ParameterizedType) key.field.getGenericType();
        return (Class) type.getActualTypeArguments()[0];
    }
}
