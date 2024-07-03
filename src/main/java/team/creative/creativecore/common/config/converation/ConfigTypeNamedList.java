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
import team.creative.creativecore.common.config.key.ConfigKeyCache;
import team.creative.creativecore.common.config.key.ConfigKeyCacheType;
import team.creative.creativecore.common.config.premade.NamedList;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;

public class ConfigTypeNamedList<T extends NamedList> extends ConfigTypeConveration<T> {
    
    protected void addToList(T list, String name, Object object) {
        list.put(name, object);
    }
    
    protected T create(ConfigKey key) {
        var result = ConfigTypeConveration.createCollection(key);
        if (result != null)
            return (T) result;
        return (T) new NamedList<>();
    }
    
    @Override
    public T readElement(HolderLookup.Provider provider, NamedList defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        T list = create(key);
        
        if (element.isJsonObject()) {
            JsonObject object = (JsonObject) element;
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                listKey.read(provider, loadDefault, ignoreRestart, entry.getValue(), side);
                addToList(list, entry.getKey(), listKey.get());
            }
            return list;
        }
        
        for (Entry<String, T> entry : (Iterable<Entry<String, T>>) defaultValue.entrySet()) {
            listKey.set(entry.getValue(), side);
            addToList(list, entry.getKey(), listKey.copy(provider, side));
        }
        return list;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, T value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonObject array = new JsonObject();
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            listKey.set(entry.getValue(), side);
            listKey.write(provider, saveDefault, ignoreRestart, side);
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", true, new ArrayList<>()).setDim(50, 130).setExpandable();
        listBox.canBeModified = x -> !x.defaultHolder;
        parent.add(listBox);
        
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        parent.add(new GuiButton("add", x -> {
            listKey.set(ConfigTypeConveration.createObject(listKey), Side.SERVER);
            var c = listKey.create(configParent, "");
            c.addNameTextfield("");
            listKey.load(configParent, c);
            listBox.addItem(c);
        }).setTranslate("gui.add"));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(T value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        loadValue(readElement(configParent.provider(), (NamedList) key.get(), true, false, writeElement(configParent.provider(), value, true, false, Side.SERVER, key), Side.SERVER,
            key), value, parent, configParent, key);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(T value, T defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) value.entrySet()) {
            listKey.set(entry.getValue(), Side.SERVER);
            var c = listKey.create(configParent, "");
            if (entry.getKey().equals("default"))
                c.addNameUnmodifieable(entry.getKey());
            else
                c.addNameTextfield(entry.getKey());
            listKey.load(configParent, c);
            c.defaultHolder = entry.getKey().equals("default");
            controls.add(c);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected T saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        T value = create(key);
        for (int i = 0; i < box.size(); i++) {
            listKey.save(box.get(i), configParent);
            addToList(value, box.get(i).getName(), listKey.get());
        }
        return value;
    }
    
    @Override
    public T set(ConfigKey key, T value) {
        return value;
    }
    
    @Override
    public boolean areEqual(T one, T two, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        ConfigTypeConveration converation = listKey instanceof ConfigKeyCacheType t ? t.converation : null;
        
        if (one.size() != two.size())
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.get(entry.getKey());
            
            if (converation != null && !converation.areEqual(entry.getValue(), other, null))
                return false;
            
            if (converation == null && !entry.getValue().equals(other))
                return false;
        }
        
        return true;
    }
}
