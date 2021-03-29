package com.creativemd.creativecore.common.config.converation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControlHolder;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.premade.NamedList;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeNamedList extends ConfigTypeConveration<NamedList> {
    
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
    
    protected void addToList(NamedList list, String name, Object object) {
        list.put(name, object);
    }
    
    protected NamedList create(Class clazz) {
        return new NamedList<>();
    }
    
    @Override
    public NamedList readElement(NamedList defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKeyField key) {
        if (element.isJsonObject()) {
            JsonObject object = (JsonObject) element;
            Class clazz = getListType(key);
            NamedList list = create(clazz);
            ConfigTypeConveration conversation = getUnsafe(clazz);
            
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                if (conversation != null)
                    addToList(list, entry.getKey(), conversation.readElement(ConfigTypeConveration.createObject(clazz), loadDefault, ignoreRestart, entry.getValue(), side, null));
                else {
                    Object value = constructEmpty(clazz);
                    holderConveration
                        .readElement(constructHolder(side, value), loadDefault, ignoreRestart, entry.getValue(), side, null);
                    addToList(list, entry.getKey(), value);
                }
            }
            
            return list;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(NamedList value, NamedList defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKeyField key) {
        JsonObject array = new JsonObject();
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet())
            if (conversation != null)
                array.add(entry.getKey(), conversation.writeElement(entry.getValue(), null, saveDefault, ignoreRestart, side, key));
            else
                array.add(entry.getKey(), holderConveration.writeElement(constructHolder(side, entry.getValue()), null, saveDefault, ignoreRestart, side, key));
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
                    converation.createControls(control, null, subClass, Math.min(100, control.width - 35));
                    control.addNameTextfield("");
                } else {
                    Object value = constructEmpty(subClass);
                    ConfigHolderObject holder = constructHolder(Side.SERVER, value);
                    control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, holder, value);
                    ((GuiConfigSubControlHolder) control).createControls();
                    control.addNameTextfield("");
                }
                listBox.add(control);
            }
        });
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void loadValue(NamedList value, GuiParent parent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = getListType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        int parentWidth = parent.width - 10;
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        int i = 0;
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
                converation.createControls(control, null, clazz, Math.min(100, control.width - 35));
                converation.loadValue(entry.getValue(), control, null);
                control.addNameTextfield(entry.getKey());
            } else {
                control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, constructHolder(Side.SERVER, entry.getValue()), entry.getValue());
                ((GuiConfigSubControlHolder) control).createControls();
                control.addNameTextfield(entry.getKey());
            }
            controls.add(control);
            i++;
        }
        
        box.addAll(controls);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    protected NamedList saveValue(GuiParent parent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        NamedList value = create(subClass);
        for (int i = 0; i < box.size(); i++)
            if (converation != null)
                addToList(value, box.get(i).getName(), converation.save(box.get(i), subClass, null));
            else {
                ((GuiConfigSubControlHolder) box.get(i)).save();
                addToList(value, box.get(i).getName(), ((GuiConfigSubControlHolder) box.get(i)).value);
            }
        return value;
    }
    
    @Override
    public NamedList set(ConfigKeyField key, NamedList value) {
        return value;
    }
    
    public Class getListType(ConfigKeyField key) {
        ParameterizedType type = (ParameterizedType) key.field.getGenericType();
        return (Class) type.getActualTypeArguments()[0];
    }
}
