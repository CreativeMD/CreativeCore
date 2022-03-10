package team.creative.creativecore.common.util.ingredient;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiStateButton;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextListBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.list.PairList;

@OnlyIn(value = Dist.CLIENT)
public abstract class GuiCreativeIngredientHandler {
    
    private static PairList<String, GuiCreativeIngredientHandler> handlers = new PairList<>();
    
    public static void registerGuiInfoHandler(String name, GuiCreativeIngredientHandler handler) {
        handler.name = name;
        handlers.add(name, handler);
    }
    
    public static int indexOf(String name) {
        return handlers.indexOfKey(name);
    }
    
    public static GuiCreativeIngredientHandler get(int index) {
        return handlers.get(index).value;
    }
    
    public static Set<String> getNames() {
        return handlers.keys();
    }
    
    public static GuiCreativeIngredientHandler getHandler(CreativeIngredient info) {
        if (info != null) {
            for (Iterator<GuiCreativeIngredientHandler> iterator = handlers.values().iterator(); iterator.hasNext();) {
                GuiCreativeIngredientHandler handler = iterator.next();
                if (handler.canHandle(info))
                    return handler;
            }
        }
        return GuiCreativeIngredientHandler.defaultHandler;
    }
    
    public static GuiCreativeIngredientHandler getHandler(String name) {
        GuiCreativeIngredientHandler handler = handlers.getValue(name);
        if (handler == null)
            return defaultHandler;
        return handler;
    }
    
    public static GuiCreativeIngredientHandler defaultHandler;
    
    static {
        defaultHandler = new GuiCreativeIngredientHandler() {
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                GuiStackSelector selector = (GuiStackSelector) new GuiStackSelector("inv", null, new GuiStackSelector.CreativeCollector(new GuiStackSelector.SearchSelector()))
                        .setExpandableX();
                gui.add(selector);
                
                gui.add(new GuiLabel("guilabel1"));
                gui.add(new GuiLabel("guilabel2"));
                
                GuiStateButton damage = new GuiStateButton("damage", 0, new TextListBuilder().add("Damage: Off", "Damage: On"));
                gui.add(damage);
                GuiStateButton nbt = new GuiStateButton("nbt", 0, new TextListBuilder().add("NBT: Off", "NBT: On"));
                gui.add(nbt);
                
                if (info instanceof CreativeIngredientBlock || info instanceof CreativeIngredientItem || info instanceof CreativeIngredientItemStack) {
                    selector.setSelectedForce(info.getExample().copy());
                    if (info instanceof CreativeIngredientItemStack) {
                        damage.nextState();
                        if (((CreativeIngredientItemStack) info).needNBT)
                            nbt.nextState();
                    }
                }
                
                onChanged(gui, new GuiControlChangedEvent(selector));
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientBlock || info instanceof CreativeIngredientItem || info instanceof CreativeIngredientItemStack;
            }
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                ItemStack stack = ((GuiStackSelector) gui.get("inv")).getSelected();
                if (stack != null) {
                    boolean damage = ((GuiStateButton) gui.get("damage")).getState() == 1;
                    boolean nbt = ((GuiStateButton) gui.get("nbt")).getState() == 1;
                    if (damage) {
                        return new CreativeIngredientItemStack(stack.copy(), nbt);
                    } else {
                        if (!(Block.byItem(stack.getItem()) instanceof AirBlock))
                            return new CreativeIngredientBlock(Block.byItem(stack.getItem()));
                        else
                            return new CreativeIngredientItem(stack.getItem());
                    }
                }
                return null;
            }
            
            @Override
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("inv")) {
                    GuiStackSelector selector = (GuiStackSelector) event.control;
                    if (selector != null) {
                        ItemStack stack = selector.getSelected();
                        if (!stack.isEmpty()) {
                            ((GuiLabel) gui.get("guilabel1")).setTitle(new TextComponent("damage: " + stack.getDamageValue()));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(new TextComponent("nbt: " + stack.getTag()));
                        } else {
                            ((GuiLabel) gui.get("guilabel1")).setTitle(new TextComponent(""));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(new TextComponent(""));
                        }
                    }
                }
            }
        };
        registerGuiInfoHandler("Default", defaultHandler);
        
        registerGuiInfoHandler("Material", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                ItemStack blockStack = ((GuiStackSelector) gui.get("inv")).getSelected();
                if (blockStack != null) {
                    Block block = Block.byItem(blockStack.getItem());
                    if (!(block instanceof AirBlock))
                        return new CreativeIngredientMaterial(block.defaultBlockState().getMaterial());
                }
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                GuiStackSelector selector = new GuiStackSelector("inv", null, new GuiStackSelector.CreativeCollector(new GuiStackSelector.GuiBlockSelector()));
                selector.setExpandableX();
                gui.add(selector);
                if (info instanceof CreativeIngredientMaterial)
                    selector.setSelectedForce(info.getExample());
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientMaterial;
            }
        });
        
        registerGuiInfoHandler("Blocktag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBoxMapped<TagKey<Block>> box = (GuiComboBoxMapped<TagKey<Block>>) gui.get("tag");
                TagKey<Block> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientBlockTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                @SuppressWarnings("deprecation")
                GuiComboBoxMapped<TagKey<Block>> box = new GuiComboBoxMapped<>("tag", new TextMapBuilder<TagKey<Block>>()
                        .addComponents(Registry.BLOCK.getTagNames().toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            @SuppressWarnings("deprecation")
                            Optional<Named<Block>> tag = Registry.BLOCK.getTag(x);
                            if (tag.isPresent() && tag.get().size() > 0)
                                builder.stack(new ItemStack(tag.get().get(0).value()));
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
            @SuppressWarnings("deprecation")
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<TagKey<Block>> box = (GuiComboBoxMapped<TagKey<Block>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<TagKey<Block>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(Registry.BLOCK.getTagNames().toList(), x -> {
                                TextBuilder builder = new TextBuilder();
                                Optional<Named<Block>> tag = Registry.BLOCK.getTag(x);
                                if (tag.isPresent() && tag.get().size() > 0)
                                    builder.stack(new ItemStack(tag.get().get(0).value()));
                                return builder.text(x.location().toString()).build();
                            }));
                }
            }
        });
        
        registerGuiInfoHandler("Itemtag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBoxMapped<TagKey<Item>> box = (GuiComboBoxMapped<TagKey<Item>>) gui.get("tag");
                TagKey<Item> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientItemTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                @SuppressWarnings("deprecation")
                GuiComboBoxMapped<TagKey<Item>> box = new GuiComboBoxMapped<>("tag", new TextMapBuilder<TagKey<Item>>().addComponents(Registry.ITEM.getTagNames().toList(), x -> {
                    TextBuilder builder = new TextBuilder();
                    @SuppressWarnings("deprecation")
                    Optional<Named<Item>> tag = Registry.ITEM.getTag(x);
                    if (tag.isPresent() && tag.get().size() > 0)
                        builder.stack(new ItemStack(tag.get().get(0).value()));
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
            @SuppressWarnings("deprecation")
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<TagKey<Item>> box = (GuiComboBoxMapped<TagKey<Item>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<TagKey<Item>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(Registry.ITEM.getTagNames().toList(), x -> {
                                TextBuilder builder = new TextBuilder();
                                Optional<Named<Item>> tag = Registry.ITEM.getTag(x);
                                if (tag.isPresent() && tag.get().size() > 0)
                                    builder.stack(new ItemStack(tag.get().get(0).value()));
                                return builder.text(x.location().toString()).build();
                            }));
                }
            }
        });
        
        registerGuiInfoHandler("Fuel", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                return new CreativeIngredientFuel();
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.add(new GuiLabel("info").setTitle(new TextComponent("Nothing to select")));
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientFuel;
            }
        });
    }
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public abstract boolean canHandle(CreativeIngredient info);
    
    public abstract void createControls(GuiParent gui, CreativeIngredient info);
    
    public abstract CreativeIngredient parseControls(GuiParent gui);
    
    public void onChanged(GuiParent gui, GuiControlChangedEvent event) {}
    
}
