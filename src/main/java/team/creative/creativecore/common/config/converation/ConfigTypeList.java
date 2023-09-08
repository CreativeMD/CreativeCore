package team.creative.creativecore.common.config.converation;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeList extends ConfigTypeConveration<List> {
    
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
                    Object value = ConfigTypeConveration.createObject(clazz);
                    holderConveration.readElement(ConfigHolderObject.createUnrelated(side, value, value), loadDefault, ignoreRestart, array.get(i), side, null);
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
                array.add(conversation.writeElement(value.get(i), null, true, ignoreRestart, side, null));
            else
                array.add(holderConveration.writeElement(ConfigHolderObject.createUnrelated(side, value.get(i), value.get(i)), null, true, ignoreRestart, side, null));
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
        parent.flow = GuiFlow.STACK_Y;
        GuiListBoxBase<GuiConfigSubControl> listBox = (GuiListBoxBase<GuiConfigSubControl>) new GuiListBoxBase<>("data", true, new ArrayList<>()).setDim(50, 130).setExpandable();
        parent.add(listBox);
        listBox.spacing = -1;
        
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        parent.add(new GuiButton("add", x -> {
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + 0);
                converation.createControls(control, null, null, subClass);
            } else {
                Object value = ConfigTypeConveration.createObject(subClass);
                ConfigHolderObject holder = ConfigHolderObject.createUnrelated(Side.SERVER, value, value);
                control = new GuiConfigSubControlHolder("" + 0, holder, value, configParent::changed);
                ((GuiConfigSubControlHolder) control).createControls();
            }
            listBox.addItem(control);
        }).setTitle(Component.translatable("gui.add")));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void restoreDefault(List value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        loadValue(readElement(value, true, false, writeElement(value, value, true, false, Side.SERVER, key), Side.SERVER, key), parent, configParent, key);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(List value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        Class clazz = getListType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        for (int i = 0; i < value.size(); i++) {
            Object entry = value.get(i);
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + i);
                converation.createControls(control, null, null, clazz);
                converation.loadValue(entry, control, null, null);
            } else {
                entry = copy(Side.SERVER, entry, clazz);
                control = new GuiConfigSubControlHolder("" + 0, ConfigHolderObject.createUnrelated(Side.SERVER, entry, entry), entry, configParent::changed);
                ((GuiConfigSubControlHolder) control).createControls();
            }
            controls.add(control);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected List saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        List value = new ArrayList(box.size());
        for (int i = 0; i < box.size(); i++)
            if (converation != null)
                value.add(converation.save(box.get(i), null, subClass, null));
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
    
    @Override
    public boolean areEqual(List one, List two, @Nullable ConfigKeyField key) {
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (one.size() != two.size())
            return false;
        
        for (int i = 0; i < one.size(); i++) {
            Object entryOne = one.get(i);
            Object entryTwo = two.get(i);
            
            if (conversation != null && !conversation.areEqual(entryOne, entryTwo, null))
                return false;
            
            if (conversation == null && !entryOne.equals(entryTwo))
                return false;
        }
        
        return true;
    }
    
}
