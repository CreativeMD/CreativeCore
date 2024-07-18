package team.creative.creativecore.common.config.converation;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeList extends ConfigTypeConveration<List> {
    
    @Override
    public List readElement(HolderLookup.Provider provider, List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        if (element.isJsonArray()) {
            JsonArray array = (JsonArray) element;
            List list = createList(key, array.size());
            for (int i = 0; i < array.size(); i++) {
                listKey.read(provider, true, ignoreRestart, array.get(i), side);
                list.add(listKey.copy(provider, side));
            }
            return list;
        }
        List list = createList(key, defaultValue.size());
        for (int i = 0; i < defaultValue.size(); i++) {
            listKey.forceValue(defaultValue.get(i), side);
            list.add(listKey.copy(provider, side));
        }
        return list;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, List value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonArray array = new JsonArray();
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        for (int i = 0; i < value.size(); i++) {
            listKey.forceValue(value.get(i), side);
            array.add(listKey.write(provider, true, ignoreRestart, side));
        }
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        parent.flow = GuiFlow.STACK_Y;
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", true, createList(key, 0)).setDim(50, 130).setExpandable();
        parent.add(listBox);
        listBox.spacing = -1;
        
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        parent.add(new GuiButton("add", x -> {
            listKey.forceValue(ConfigTypeConveration.createObject(listKey.field()), side);
            var c = listKey.create(configParent, "", side);
            listKey.load(configParent, c, side);
            listBox.addItem(c);
        }).setTitle(Component.translatable("gui.add")));
        
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(List value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        loadValue(readElement(configParent.provider(), (List) key.get(), true, false, writeElement(configParent.provider(), value, true, false, side, key), side, key), value,
            parent, configParent, key, side);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(List value, List defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        List<GuiConfigSubControl> controls = createList(key, value.size());
        for (int i = 0; i < value.size(); i++) {
            listKey.forceValue(value.get(i), side);
            listKey.forceValue(listKey.copy(configParent.provider(), side), side); // Make sure the object does not get modified by saving the control
            var c = listKey.create(configParent, "", side);
            listKey.load(configParent, c, side);
            controls.add(c);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected List saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        
        GuiListBoxBase<GuiConfigSubControl> box = parent.get("data");
        List value = createList(key, box.size());
        for (int i = 0; i < box.size(); i++) {
            listKey.save(box.get(i), configParent, side);
            value.add(listKey.copy(configParent.provider(), side));
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
    public boolean areEqual(List one, List two, ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        ConfigTypeConveration converation = listKey.converation();
        
        if (one.size() != two.size())
            return false;
        
        for (int i = 0; i < one.size(); i++) {
            Object entryOne = one.get(i);
            Object entryTwo = two.get(i);
            
            if (converation != null) {
                listKey.forceValue(entryOne, side);
                if (!converation.areEqual(entryOne, entryTwo, listKey, side))
                    return false;
                
            } else if (converation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
    
}
