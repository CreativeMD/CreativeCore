package team.creative.creativecore.common.config.converation;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyCache;
import team.creative.creativecore.common.config.key.ConfigKeyCacheType;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeList extends ConfigTypeConveration<List> {
    
    @Override
    public List readElement(HolderLookup.Provider provider, List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            List list = createList(key, array.size());
            for (int i = 0; i < array.size(); i++) {
                listKey.read(provider, loadDefault, ignoreRestart, array.get(i), side);
                list.add(listKey.get());
            }
            return list;
        }
        List list = createList(key, defaultValue.size());
        for (int i = 0; i < defaultValue.size(); i++) {
            listKey.set(defaultValue.get(i), side);
            list.add(listKey.copy(provider, side));
        }
        return list;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, List value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonArray array = new JsonArray();
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        for (int i = 0; i < value.size(); i++) {
            listKey.set(value.get(i), side);
            array.add(listKey.write(provider, saveDefault, ignoreRestart, side));
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        parent.flow = GuiFlow.STACK_Y;
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", true, createList(key, 0)).setDim(50, 130).setExpandable();
        parent.add(listBox);
        listBox.spacing = -1;
        
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        parent.add(new GuiButton("add", x -> {
            listKey.set(ConfigTypeConveration.createObject(listKey), Side.SERVER);
            var c = listKey.create(configParent, "");
            listKey.load(configParent, c);
            listBox.addItem(c);
        }).setTitle(Component.translatable("gui.add")));
        
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(List value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        loadValue(readElement(configParent.provider(), (List) key.get(), true, false, writeElement(configParent.provider(), value, true, false, Side.SERVER, key), Side.SERVER,
            key), value, parent, configParent, key);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(List value, List defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        
        List<GuiConfigSubControl> controls = createList(key, value.size());
        for (int i = 0; i < value.size(); i++) {
            listKey.set(value.get(i), Side.SERVER);
            var c = listKey.create(configParent, "");
            listKey.load(configParent, c);
            controls.add(c);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected List saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        List value = createList(key, box.size());
        for (int i = 0; i < box.size(); i++) {
            listKey.save(box.get(i), configParent);
            value.add(listKey.get());
        }
        return value;
    }
    
    @Override
    public List set(ConfigKey key, List value) {
        return value;
    }
    
    public List createList(ConfigKey key, int expectedSize) {
        var result = ConfigTypeConveration.createCollection(key);
        if (result instanceof List l)
            return l;
        return new ArrayList<>(expectedSize);
    }
    
    @Override
    public boolean areEqual(List one, List two, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        ConfigTypeConveration converation = listKey instanceof ConfigKeyCacheType t ? t.converation : null;
        
        if (one.size() != two.size())
            return false;
        
        for (int i = 0; i < one.size(); i++) {
            Object entryOne = one.get(i);
            Object entryTwo = two.get(i);
            
            if (converation != null && !converation.areEqual(entryOne, entryTwo, null))
                return false;
            
            if (converation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
    
}
