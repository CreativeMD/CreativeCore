package team.creative.creativecore.common.config.converation.registry;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.registry.RegistryObjectListConfig;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeRegistryObjectList extends ConfigTypeConveration<RegistryObjectListConfig> {
    
    @Override
    public RegistryObjectListConfig readElement(HolderLookup.Provider provider, RegistryObjectListConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        if (element.isJsonArray()) {
            RegistryObjectListConfig list = new RegistryObjectListConfig(defaultValue.registry);
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++)
                list.add(new ResourceLocation(array.get(i).getAsString()));
            return list;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, RegistryObjectListConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonArray array = new JsonArray(value.size());
        for (ResourceLocation location : (Iterable<ResourceLocation>) value.locations())
            array.add(location.toString());
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        parent.flow = GuiFlow.STACK_Y;
        GuiListBoxBase listBox = new GuiListBoxBase<>("data", true, new ArrayList<>());
        parent.add(listBox.setDim(50, 130).setExpandable());
        listBox.spacing = -1;
        
        parent.add(new GuiButton("add", null).setTitle(Component.translatable("gui.add")));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(RegistryObjectListConfig value, RegistryObjectListConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiListBoxBase listBox = parent.get("data");
        if (!listBox.isEmpty())
            listBox.clearItems();
        
        configParent.setCustomData(value.registry);
        
        GuiButton add = parent.get("add");
        add.setPressed(x -> {
            GuiParent entry = new GuiParent().setAlign(Align.STRETCH);
            GuiRegistryObjectHandler.REGISTRY.get(value.registry).createControls(entry, value.registry);
            listBox.addItem(entry.setExpandableX());
        });
        
        for (ResourceLocation location : (Iterable<ResourceLocation>) value.locations()) {
            GuiParent entry = new GuiParent().setAlign(Align.STRETCH);
            GuiRegistryObjectHandler.REGISTRY.get(value.registry).createControls(entry, value.registry);
            GuiRegistryObjectHandler.REGISTRY.get(value.registry).loadValue(entry, value.registry, location);
            listBox.addItem(entry.setExpandableX());
        }
        
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected RegistryObjectListConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        RegistryObjectListConfig list = new RegistryObjectListConfig<>((Registry) configParent.getCustomData());
        
        GuiListBoxBase<GuiParent> listBox = parent.get("data");
        for (int i = 0; i < listBox.size(); i++)
            list.add(GuiRegistryObjectHandler.REGISTRY.get(list.registry).saveValue(listBox.get(i), list.registry));
        
        return list;
    }
    
    @Override
    public RegistryObjectListConfig set(ConfigKey key, RegistryObjectListConfig value) {
        return value;
    }
    
    @Override
    public boolean areEqual(RegistryObjectListConfig one, RegistryObjectListConfig two, ConfigKey key, Side side) {
        if (one.size() != two.size())
            return false;
        
        if (one.registry != two.registry)
            return false;
        
        List<ResourceLocation> copy = new ArrayList<>();
        for (ResourceLocation location : (Iterable<ResourceLocation>) two.locations())
            copy.add(location);
        for (int i = 0; i < one.size(); i++)
            if (!copy.remove(one.getLocation(i)))
                return false;
        return copy.isEmpty();
    }
    
}
