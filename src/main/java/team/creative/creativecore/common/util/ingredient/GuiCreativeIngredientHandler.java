package team.creative.creativecore.common.util.ingredient;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.collection.GuiCheckList;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;

@Environment(EnvType.CLIENT)
public abstract class GuiCreativeIngredientHandler {
    
    public static final NamedHandlerRegistry<GuiCreativeIngredientHandler> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static GuiCreativeIngredientHandler find(CreativeIngredient info) {
        if (info != null)
            for (GuiCreativeIngredientHandler handler : REGISTRY.values())
                if (handler.canHandle(info))
                    return handler;
        return REGISTRY.getDefault();
    }
    
    static {
        REGISTRY.registerDefault("Default", new GuiCreativeIngredientHandler() {
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                GuiStackSelector selector = (GuiStackSelector) new GuiStackSelector("inv", gui
                        .getPlayer(), new GuiStackSelector.CreativeCollector(new GuiStackSelector.SearchSelector())).setExpandableX();
                gui.add(selector);
                
                gui.add(new GuiDataCheckList("list", false, null, info instanceof CreativeIngredientItemStack s ? s.getIncluded() : new ArrayList<>()).setExpandable());
                
                if (info instanceof CreativeIngredientBlock || info instanceof CreativeIngredientItem || info instanceof CreativeIngredientItemStack)
                    selector.setSelectedForce(info.getExample().copy());
                
                onChanged(gui, new GuiControlChangedEvent(selector));
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientBlock || info instanceof CreativeIngredientItem || info instanceof CreativeIngredientItemStack;
            }
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                ItemStack stack = gui.get("inv", GuiStackSelector.class).getSelected();
                if (stack != null) {
                    GuiDataCheckList list = gui.get("list");
                    var included = list.getConfiguredIncluded();
                    if (included.isEmpty()) {
                        if (!(Block.byItem(stack.getItem()) instanceof AirBlock))
                            return new CreativeIngredientBlock(Block.byItem(stack.getItem()));
                        else
                            return new CreativeIngredientItem(stack.getItem());
                    }
                    return new CreativeIngredientItemStack(stack, included);
                }
                return null;
            }
            
            @Override
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("inv")) {
                    if (event.control instanceof GuiStackSelector selector) {
                        GuiDataCheckList list = gui.get("list");
                        ItemStack stack = selector.getSelected();
                        TextMapBuilder<DataComponentType<?>> map = new TextMapBuilder<>();
                        Object2BooleanMap<DataComponentType<?>> selected = new Object2BooleanArrayMap<>();
                        CompoundTag nbt = (CompoundTag) ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack).getOrThrow();
                        nbt = nbt.getCompoundOrEmpty("components");
                        if (!nbt.isEmpty())
                            for (String component : nbt.keySet()) {
                                var location = ResourceLocation.parse(component);
                                var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(location);
                                map.addComponent(type, Component.literal(component).append(": ").append(Component.literal(nbt.get(component).asString().get()).withStyle(
                                    ChatFormatting.GRAY)));
                                if (list.includes(type))
                                    selected.put(type, true);
                            }
                        list.set(map, selected);
                        gui.reflow();
                    }
                }
            }
        });
        
        REGISTRY.register("Blocktag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBox<TagKey<Block>> box = gui.get("tag");
                TagKey<Block> tag = box.selected();
                if (tag != null)
                    return new CreativeIngredientBlockTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                GuiComboBox<TagKey<Block>> box = new GuiComboBox<>("tag", new TextMapBuilder<TagKey<Block>>().addComponents(BuiltInRegistries.BLOCK.getTags().map(x -> x.unwrapKey()
                        .get()).toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            var itr = BuiltInRegistries.BLOCK.getTagOrEmpty(x).iterator();
                            if (itr.hasNext())
                                builder.stack(new ItemStack(itr.next().value()));
                            return builder.text(x.location().toString()).build();
                        }));
                gui.add(box);
                gui.add(new GuiTextfield("search"));
                if (info instanceof CreativeIngredientBlockTag)
                    box.select(((CreativeIngredientBlockTag) info).tag);
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientBlockTag;
            }
            
            @Override
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBox<TagKey<Block>> box = gui.get("tag");
                    box.set(new TextMapBuilder<TagKey<Block>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText())).addComponents(
                        BuiltInRegistries.BLOCK.getTags().map(x -> x.unwrapKey().get()).toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            var itr = BuiltInRegistries.BLOCK.getTagOrEmpty(x).iterator();
                            if (itr.hasNext())
                                builder.stack(new ItemStack(itr.next().value()));
                            return builder.text(x.location().toString()).build();
                        }));
                }
            }
        });
        
        REGISTRY.register("Itemtag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBox<TagKey<Item>> box = gui.get("tag");
                TagKey<Item> tag = box.selected();
                if (tag != null)
                    return new CreativeIngredientItemTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                GuiComboBox<TagKey<Item>> box = new GuiComboBox<>("tag", new TextMapBuilder<TagKey<Item>>().addComponents(BuiltInRegistries.ITEM.getTags().map(x -> x.unwrapKey()
                        .get()).toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            var itr = BuiltInRegistries.ITEM.getTagOrEmpty(x).iterator();
                            if (itr.hasNext())
                                builder.stack(new ItemStack(itr.next().value()));
                            return builder.text(x.location().toString()).build();
                        }));
                gui.add(box);
                gui.add(new GuiTextfield("search"));
                if (info instanceof CreativeIngredientItemTag)
                    box.select(((CreativeIngredientItemTag) info).tag);
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientItemTag;
            }
            
            @Override
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBox<TagKey<Item>> box = gui.get("tag");
                    box.set(new TextMapBuilder<TagKey<Item>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText())).addComponents(
                        BuiltInRegistries.ITEM.getTags().map(x -> x.unwrapKey().get()).toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            var itr = BuiltInRegistries.ITEM.getTagOrEmpty(x).iterator();
                            if (itr.hasNext())
                                builder.stack(new ItemStack(itr.next().value()));
                            return builder.text(x.location().toString()).build();
                        }));
                }
            }
        });
        
        REGISTRY.register("Fuel", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                return new CreativeIngredientFuel();
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.add(new GuiLabel("info").setTitle(Component.literal("Nothing to select")));
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientFuel;
            }
        });
    }
    
    public abstract boolean canHandle(CreativeIngredient info);
    
    public abstract void createControls(GuiParent gui, CreativeIngredient info);
    
    public abstract CreativeIngredient parseControls(GuiParent gui);
    
    public void onChanged(GuiParent gui, GuiControlChangedEvent event) {}
    
    public static class GuiDataCheckList extends GuiCheckList<DataComponentType<?>> {
        
        public List<ResourceLocation> included;
        
        public GuiDataCheckList(String name, boolean modifiable, TextMapBuilder<DataComponentType<?>> map, List<ResourceLocation> included) {
            super(name, modifiable, map, null);
            this.included = included;
        }
        
        public boolean includes(DataComponentType<?> type) {
            return included.contains(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type));
        }
        
        public List<ResourceLocation> getConfiguredIncluded() {
            List<ResourceLocation> included = new ArrayList<>();
            for (GuiCheckList<DataComponentType<?>>.GuiCheckListRow row : rows)
                if (row.checkBox.value)
                    included.add(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(row.value));
            return included;
        }
        
    }
    
}
