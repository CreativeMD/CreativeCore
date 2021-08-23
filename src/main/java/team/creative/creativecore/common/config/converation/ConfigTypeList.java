package team.creative.creativecore.common.config.converation;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.GuiConfigSubControl;
import team.creative.creativecore.common.config.gui.GuiConfigSubControlHolder;
import team.creative.creativecore.common.config.holder.ConfigHolderObject;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.controls.GuiListBoxBase;

public class ConfigTypeList extends ConfigTypeConveration<List> {
    
    private ConfigHolderObject constructHolder(Dist side, Object value) {
        return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
    }
    
    @Override
    public List readElement(List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Dist side, @Nullable ConfigKeyField key) {
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
                    holderConveration.readElement(constructHolder(side, value), loadDefault, ignoreRestart, array.get(i), side, null);
                    list.add(value);
                }
            return list;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, boolean ignoreRestart, Dist side, @Nullable ConfigKeyField key) {
        JsonArray array = new JsonArray();
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        for (int i = 0; i < value.size(); i++)
            if (conversation != null)
                array.add(conversation.writeElement(value.get(i), null, true, ignoreRestart, side, key));
            else
                array.add(holderConveration.writeElement(constructHolder(side, value.get(i)), null, true, ignoreRestart, side, key));
        return array;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void createControls(GuiParent parent, @Nullable ConfigKeyField key, Class clazz, int recommendedWidth) {
        parent.setHeight(160);
        GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<>("data", 0, 0, parent.getWidth() - 10, 130, true, new ArrayList<>());
        parent.add(listBox);
        
        Class subClass = getListType(key);
        ConfigTypeConveration converation = getUnsafe(subClass);
        
        int parentWidth = parent.getWidth() - 10;
        
        parent.add(new GuiButton("add", 0, 140, x -> {
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + 0, 2, 0, parentWidth, 14);
                converation.createControls(control, null, subClass, control.getWidth() - 35);
            } else {
                Object value = ConfigTypeConveration.createObject(subClass);
                ConfigHolderObject holder = constructHolder(Dist.DEDICATED_SERVER, value);
                control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, holder, value);
                ((GuiConfigSubControlHolder) control).createControls();
            }
            listBox.addItem(control);
        }).setTitle(new TranslatableComponent("gui.add")));
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void loadValue(List value, GuiParent parent, @Nullable ConfigKeyField key) {
        GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase<GuiConfigSubControl>) parent.get("data");
        if (!box.isEmpty())
            box.clearItems();
        
        Class clazz = getListType(key);
        ConfigTypeConveration converation = getUnsafe(clazz);
        
        int parentWidth = parent.getWidth() - 10;
        
        List<GuiConfigSubControl> controls = new ArrayList<>(value.size());
        for (int i = 0; i < value.size(); i++) {
            Object entry = value.get(i);
            GuiConfigSubControl control;
            if (converation != null) {
                control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
                converation.createControls(control, null, clazz, control.getWidth() - 35);
                converation.loadValue(entry, control, null);
            } else {
                control = new GuiConfigSubControlHolder("" + 0, 2, 0, parentWidth, 14, constructHolder(Dist.DEDICATED_SERVER, entry), entry);
                ((GuiConfigSubControlHolder) control).createControls();
            }
            controls.add(control);
        }
        
        box.addAllItems(controls);
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
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
