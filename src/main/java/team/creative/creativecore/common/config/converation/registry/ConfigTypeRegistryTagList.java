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
import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.registry.RegistryTagListConfig;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiListBoxBase;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class ConfigTypeRegistryTagList extends ConfigTypeConveration<RegistryTagListConfig> {
    
    @Override
    public RegistryTagListConfig readElement(HolderLookup.Provider provider, RegistryTagListConfig defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, ConfigKey key) {
        if (element.isJsonArray()) {
            RegistryTagListConfig list = new RegistryTagListConfig(defaultValue.registry);
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++)
                list.add(TagKey.create(defaultValue.registry.key(), ResourceLocation.parse(array.get(i).getAsString())));
            return list;
        }
        return defaultValue;
    }
    
    @Override
    public JsonElement writeElement(HolderLookup.Provider provider, RegistryTagListConfig value, boolean saveDefault, boolean ignoreRestart, Side side, ConfigKey key) {
        JsonArray array = new JsonArray(value.size());
        for (TagKey tag : (Iterable<TagKey>) value)
            array.add(tag.location().toString());
        return array;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        parent.flow = GuiFlow.STACK_Y;
        GuiListBoxBase listBox = new GuiListBoxBase<>("data", true, new ArrayList<>());
        parent.add(listBox.setDim(50, 130).setExpandable());
        listBox.spacing = -1;
        
        parent.add(new GuiButton("add", null).setTitle(Component.translatable("gui.add")));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(RegistryTagListConfig value, RegistryTagListConfig defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiListBoxBase listBox = parent.get("data");
        if (!listBox.isEmpty())
            listBox.clearItems();
        
        configParent.setCustomData(value.registry);
        
        GuiButton add = parent.get("add");
        add.setPressed(x -> {
            GuiParent entry = new GuiParent().setAlign(Align.STRETCH);
            GuiRegistryTagHandler.REGISTRY.get(value.registry).createControls(entry, value.registry);
            listBox.addItem(entry.setExpandableX());
        });
        
        for (TagKey tag : (Iterable<TagKey>) value) {
            GuiParent entry = new GuiParent().setAlign(Align.STRETCH);
            GuiRegistryTagHandler.REGISTRY.get(value.registry).createControls(entry, value.registry);
            GuiRegistryTagHandler.REGISTRY.get(value.registry).loadValue(entry, value.registry, tag);
            listBox.addItem(entry.setExpandableX());
        }
        
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected RegistryTagListConfig saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        RegistryTagListConfig list = new RegistryTagListConfig<>((Registry) configParent.getCustomData());
        
        GuiListBoxBase<GuiParent> listBox = parent.get("data");
        for (int i = 0; i < listBox.size(); i++)
            list.add(GuiRegistryTagHandler.REGISTRY.get(list.registry).saveValue(listBox.get(i), list.registry));
        
        return list;
    }
    
    @Override
    public RegistryTagListConfig set(ConfigKey key, RegistryTagListConfig value) {
        return value;
    }
    
    @Override
    public boolean areEqual(RegistryTagListConfig one, RegistryTagListConfig two, ConfigKey key) {
        if (one.size() != two.size())
            return false;
        
        if (one.registry != two.registry)
            return false;
        
        List<TagKey> copy = new ArrayList<>();
        for (TagKey tag : (Iterable<TagKey>) two)
            copy.add(tag);
        for (int i = 0; i < one.size(); i++)
            if (!copy.remove(one.get(i)))
                return false;
        return copy.isEmpty();
    }
    
}
