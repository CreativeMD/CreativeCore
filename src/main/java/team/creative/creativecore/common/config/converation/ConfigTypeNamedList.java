package team.creative.creativecore.common.config.converation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;

public class ConfigTypeNamedList<T extends NamedList> extends ConfigTypeConveration<T> {
    
    protected void addToList(T list, String name, Object object) {
        list.put(name, object);
    }
    
    protected T create(ConfigKey key, Side side) {
        var result = ConfigTypeConveration.createCollection(key);
        if (result != null)
            return (T) result;
        return (T) new NamedList<>();
    }
    
    @Override
    public T readElement(HolderLookup.Provider provider, NamedList defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        T list = create(key, side);
        
        if (element.isJsonObject()) {
            JsonObject object = (JsonObject) element;
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                listKey.read(provider, true, ignoreRestart, entry.getValue(), side);
                addToList(list, entry.getKey(), listKey.copy(provider, side));
            }
            return list;
        }
        
        for (Entry<String, T> entry : (Iterable<Entry<String, T>>) defaultValue.entrySet()) {
            listKey.forceValue(entry.getValue(), side);
            addToList(list, entry.getKey(), listKey.copy(provider, side));
        }
        return list;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, T value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonObject array = new JsonObject();
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            listKey.forceValue(entry.getValue(), side);
            listKey.write(provider, true, ignoreRestart, side);
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", true, new ArrayList<>()).setDim(50, 130).setExpandable();
        listBox.canBeModified = x -> !x.defaultHolder;
        parent.add(listBox);
        
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        parent.add(new GuiButton("add", x -> {
            listKey.forceValue(ConfigTypeConveration.createObject(listKey.field()), side);
            var c = listKey.create(configParent, "", side);
            c.addNameTextfield("");
            listKey.load(configParent, c, side);
            listBox.addItem(c);
        }).setTranslate("gui.add"));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        loadValue(readElement(configParent.provider(), (NamedList) key.get(), true, false, writeElement(configParent.provider(), value, true, false, side, key), side, key), value,
            parent, configParent, key, side);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            listKey.forceValue(entry.getValue(), side);
            var c = listKey.create(configParent, "", side);
            if (entry.getKey().equals("default"))
                c.addNameUnmodifieable(entry.getKey());
            else
                c.addNameTextfield(entry.getKey());
            listKey.load(configParent, c, side);
            c.defaultHolder = entry.getKey().equals("default");
            controls.add(c);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        T value = create(key, side);
        for (int i = 0; i < box.size(); i++) {
            listKey.save(box.get(i), configParent, side);
            addToList(value, box.get(i).getName(), listKey.copy(configParent.provider(), side));
        }
        return value;
    }
    
    @Override
    public T set(ConfigKey key, T value) {
        return value;
    }
    
    @Override
    public boolean areEqual(T one, T two, ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        ConfigTypeConveration converation = listKey.converation();
        
        if (one.size() != two.size())
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.get(entry.getKey());
            
            if (converation != null) {
                listKey.forceValue(entry.getValue(), side);
                if (!converation.areEqual(entry.getValue(), other, listKey, side))
                    return false;
                
            } else if (converation == null && !entry.getValue().equals(other))
                return false;
        }
        
        return true;
    }
}
