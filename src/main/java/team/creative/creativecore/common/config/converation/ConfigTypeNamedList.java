package team.creative.creativecore.common.config.converation;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;

public class ConfigTypeNamedList extends ConfigTypeConveration<NamedList> {
    
    private ConfigHolderObject constructHolder(Side side, Object value) {
        return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
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
                    Object value = ConfigTypeConveration.createObject(clazz);
                    holderConveration.readElement(constructHolder(side, value), loadDefault, ignoreRestart, entry.getValue(), side, null);
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
                array.add(entry.getKey(), conversation.writeElement(entry.getValue(), null, true, ignoreRestart, side, key));
            else
                array.add(entry.getKey(), holderConveration.writeElement(constructHolder(side, entry.getValue()), null, true, ignoreRestart, side, key));
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", 50, 130, true, new ArrayList<>()).setExpandable();
        parent.add(listBox);
        
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        parent.add(new GuiButton("add", x -> {
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + 0);
                converation.createControls(control, null, null, subClass);
                control.addNameTextfield("");
            } else {
                Object value = ConfigTypeConveration.createObject(subClass);
                ConfigHolderObject holder = constructHolder(Side.SERVER, value);
                control = new GuiConfigSubControlHolder("" + 0, holder, value);
                ((GuiConfigSubControlHolder) control).createControls();
                control.addNameTextfield("");
            }
            listBox.addItem(control);
        }));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(NamedList value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clear();
        
        Class clazz = getListType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        int i = 0;
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + i);
                converation.createControls(control, null, null, clazz);
                converation.loadValue(entry.getValue(), control, null, null);
                control.addNameTextfield(entry.getKey());
            } else {
                control = new GuiConfigSubControlHolder("" + 0, constructHolder(Side.SERVER, entry.getValue()), entry.getValue());
                ((GuiConfigSubControlHolder) control).createControls();
                control.addNameTextfield(entry.getKey());
            }
            controls.add(control);
            i++;
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected NamedList saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        NamedList value = create(subClass);
        for (int i = 0; i < box.size(); i++)
            if (converation != null)
                addToList(value, box.get(i).getName(), converation.save(box.get(i), null, subClass, null));
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
    
    @Override
    public boolean areEqual(NamedList one, NamedList two, @Nullable ConfigKeyField key) {
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (one.size() != two.size())
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.get(entry.getKey());
            
            if (conversation != null && !conversation.areEqual(entry.getValue(), other, null))
                return false;
            
            if (conversation == null && !entry.getValue().equals(other))
                return false;
        }
        
        return true;
    }
}
