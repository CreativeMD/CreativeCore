package team.creative.creativecore.common.util.ingredient;

import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextListBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
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
                    if (event.control instanceof GuiStackSelector selector) {
                        ItemStack stack = selector.getSelected();
                        if (!stack.isEmpty()) {
                            ((GuiLabel) gui.get("guilabel1")).setTitle(Component.literal("damage: " + stack.getDamageValue()));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(Component.literal("nbt: " + stack.getTag()));
                        } else {
                            ((GuiLabel) gui.get("guilabel1")).setTitle(Component.literal(""));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(Component.literal(""));
                        }
                    }
                }
            }
        });
        
        REGISTRY.register("Blocktag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBoxMapped<TagKey<Block>> box = gui.get("tag");
                TagKey<Block> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientBlockTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                GuiComboBoxMapped<TagKey<Block>> box = new GuiComboBoxMapped<>("tag", new TextMapBuilder<TagKey<Block>>().addComponents(Registry.BLOCK.getTagNames()
                        .toList(), x -> {
                            TextBuilder builder = new TextBuilder();
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
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<TagKey<Block>> box = gui.get("tag");
                    box.setLines(new TextMapBuilder<TagKey<Block>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText())).addComponents(
                        Registry.BLOCK.getTagNames().toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            Optional<Named<Block>> tag = Registry.BLOCK.getTag(x);
                            if (tag.isPresent() && tag.get().size() > 0)
                                builder.stack(new ItemStack(tag.get().get(0).value()));
                            return builder.text(x.location().toString()).build();
                        }));
                }
            }
        });
        
        REGISTRY.register("Itemtag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseControls(GuiParent gui) {
                GuiComboBoxMapped<TagKey<Item>> box = gui.get("tag");
                TagKey<Item> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientItemTag(tag);
                return null;
            }
            
            @Override
            public void createControls(GuiParent gui, CreativeIngredient info) {
                gui.flow = GuiFlow.STACK_Y;
                gui.align = Align.STRETCH;
                GuiComboBoxMapped<TagKey<Item>> box = new GuiComboBoxMapped<>("tag", new TextMapBuilder<TagKey<Item>>().addComponents(Registry.ITEM.getTagNames().toList(),
                    x -> {
                        TextBuilder builder = new TextBuilder();
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
            public void onChanged(GuiParent gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<TagKey<Item>> box = gui.get("tag");
                    box.setLines(new TextMapBuilder<TagKey<Item>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText())).addComponents(
                        Registry.ITEM.getTagNames().toList(), x -> {
                            TextBuilder builder = new TextBuilder();
                            Optional<Named<Item>> tag = Registry.ITEM.getTag(x);
                            if (tag.isPresent() && tag.get().size() > 0)
                                builder.stack(new ItemStack(tag.get().get(0).value()));
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
    
}
