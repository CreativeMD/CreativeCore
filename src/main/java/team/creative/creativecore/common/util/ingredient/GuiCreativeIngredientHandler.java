package team.creative.creativecore.common.util.ingredient;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.FullItemDialogGuiLayer;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.controls.parent.GuiYBox;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.controls.simple.GuiStateButton;
import team.creative.creativecore.common.gui.controls.simple.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.text.TextListBuilder;
import team.creative.creativecore.common.util.text.TextMapBuilder;
import team.creative.creativecore.common.util.type.PairList;

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
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiStackSelector selector = new GuiStackSelector("inv", 0, 30, 122, null, new GuiStackSelector.CreativeCollector(new GuiStackSelector.SearchSelector()));
                gui.add(selector);
                
                gui.add(new GuiLabel("guilabel1", 70, 80));
                gui.add(new GuiLabel("guilabel2", 70, 90));
                
                GuiStateButton damage = new GuiStateButton("damage", 0, 80, 0, new TextListBuilder().add("Damage: Off", "Damage: On"));
                gui.add(damage);
                GuiStateButton nbt = new GuiStateButton("nbt", 0, 100, 0, new TextListBuilder().add("NBT: Off", "NBT: On"));
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
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
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
            public void onChanged(FullItemDialogGuiLayer gui, GuiControlChangedEvent event) {
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
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                ItemStack blockStack = ((GuiStackSelector) gui.get("inv")).getSelected();
                if (blockStack != null) {
                    Block block = Block.byItem(blockStack.getItem());
                    if (!(block instanceof AirBlock))
                        return new CreativeIngredientMaterial(block.defaultBlockState().getMaterial());
                }
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiStackSelector selector = new GuiStackSelector("inv", 0, 30, 122, null, new GuiStackSelector.CreativeCollector(new GuiStackSelector.GuiBlockSelector()));
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
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                GuiComboBoxMapped<Tag<Block>> box = (GuiComboBoxMapped<Tag<Block>>) gui.get("tag");
                Tag<Block> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientBlockTag(tag);
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiYBox test = new GuiYBox("test", 0, 30, Align.STRETCH);
                GuiComboBoxMapped<Tag<Block>> box = new GuiComboBoxMapped<>("tag", 0, 30, new TextMapBuilder<Tag<Block>>().addComponents(BlockTags.getAllTags().getAllTags()
                        .values(), x -> new TextBuilder().stack(new ItemStack(x.getValues().get(0))).text(BlockTags.getAllTags().getId(x).toString()).build()));
                test.add(box);
                test.add(new GuiTextfield("search", 0, 0, 10, 16));
                gui.add(test);
                if (info instanceof CreativeIngredientBlockTag)
                    box.select(((CreativeIngredientBlockTag) info).tag);
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientBlockTag;
            }
            
            @Override
            public void onChanged(FullItemDialogGuiLayer gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<Tag<Block>> box = (GuiComboBoxMapped<Tag<Block>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<Tag<Block>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(BlockTags.getAllTags().getAllTags()
                                    .values(), x -> new TextBuilder().stack(new ItemStack(x.getValues().get(0))).text(BlockTags.getAllTags().getId(x).toString()).build()));
                }
            }
        });
        
        registerGuiInfoHandler("Itemtag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                GuiComboBoxMapped<Tag<Item>> box = (GuiComboBoxMapped<Tag<Item>>) gui.get("tag");
                Tag<Item> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientItemTag(tag);
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiYBox test = new GuiYBox("test", 0, 30, Align.STRETCH);
                GuiComboBoxMapped<Tag<Item>> box = new GuiComboBoxMapped<>("tag", 0, 30, new TextMapBuilder<Tag<Item>>().addComponents(ItemTags.getAllTags().getAllTags()
                        .values(), x -> new TextBuilder().stack(new ItemStack(x.getValues().get(0))).text(ItemTags.getAllTags().getId(x).toString()).build()));
                test.add(box);
                test.add(new GuiTextfield("search", 0, 0, 10, 16));
                gui.add(test);
                if (info instanceof CreativeIngredientItemTag)
                    box.select(((CreativeIngredientItemTag) info).tag);
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientItemTag;
            }
            
            @Override
            public void onChanged(FullItemDialogGuiLayer gui, GuiControlChangedEvent event) {
                if (event.control.is("search")) {
                    GuiComboBoxMapped<Tag<Item>> box = (GuiComboBoxMapped<Tag<Item>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<Tag<Item>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(ItemTags.getAllTags().getAllTags()
                                    .values(), x -> new TextBuilder().stack(new ItemStack(x.getValues().get(0))).text(ItemTags.getAllTags().getId(x).toString()).build()));
                }
            }
        });
        
        registerGuiInfoHandler("Fuel", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                return new CreativeIngredientFuel();
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                gui.add(new GuiLabel("info", 5, 30).setTitle(new TextComponent("Nothing to select")));
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
    
    public abstract void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info);
    
    public abstract CreativeIngredient parseInfo(FullItemDialogGuiLayer gui);
    
    public void onChanged(FullItemDialogGuiLayer gui, GuiControlChangedEvent event) {}
    
}
