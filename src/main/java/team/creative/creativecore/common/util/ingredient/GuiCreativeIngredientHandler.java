package team.creative.creativecore.common.util.ingredient;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.FullItemDialogGuiLayer;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.controls.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiStackSelector;
import team.creative.creativecore.common.gui.controls.GuiStateButton;
import team.creative.creativecore.common.gui.controls.GuiTextfield;
import team.creative.creativecore.common.gui.controls.layout.GuiVBox;
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
                        if (!(Block.getBlockFromItem(stack.getItem()) instanceof AirBlock))
                            return new CreativeIngredientBlock(Block.getBlockFromItem(stack.getItem()));
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
                            ((GuiLabel) gui.get("guilabel1")).setTitle(new StringTextComponent("damage: " + stack.getDamage()));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(new StringTextComponent("nbt: " + stack.getTag()));
                        } else {
                            ((GuiLabel) gui.get("guilabel1")).setTitle(new StringTextComponent(""));
                            ((GuiLabel) gui.get("guilabel2")).setTitle(new StringTextComponent(""));
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
                    Block block = Block.getBlockFromItem(blockStack.getItem());
                    if (!(block instanceof AirBlock))
                        return new CreativeIngredientMaterial(block.getDefaultState().getMaterial());
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
                GuiComboBoxMapped<ITag<Block>> box = (GuiComboBoxMapped<ITag<Block>>) gui.get("tag");
                ITag<Block> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientBlockTag(tag);
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiVBox test = new GuiVBox("test", 0, 30, Align.STRETCH);
                GuiComboBoxMapped<ITag<Block>> box = new GuiComboBoxMapped<>("tag", 0, 30, new TextMapBuilder<ITag<Block>>()
                        .addComponents(BlockTags.getCollection().getIDTagMap().values(), x -> new TextBuilder().stack(new ItemStack(x.getAllElements().get(0)))
                                .text(BlockTags.getCollection().getDirectIdFromTag(x).toString()).build()));
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
                    GuiComboBoxMapped<ITag<Block>> box = (GuiComboBoxMapped<ITag<Block>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<ITag<Block>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(BlockTags.getCollection().getIDTagMap().values(), x -> new TextBuilder().stack(new ItemStack(x.getAllElements().get(0)))
                                    .text(BlockTags.getCollection().getDirectIdFromTag(x).toString()).build()));
                }
            }
        });
        
        registerGuiInfoHandler("Itemtag", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                GuiComboBoxMapped<ITag<Item>> box = (GuiComboBoxMapped<ITag<Item>>) gui.get("tag");
                ITag<Item> tag = box.getSelected();
                if (tag != null)
                    return new CreativeIngredientItemTag(tag);
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiVBox test = new GuiVBox("test", 0, 30, Align.STRETCH);
                GuiComboBoxMapped<ITag<Item>> box = new GuiComboBoxMapped<>("tag", 0, 30, new TextMapBuilder<ITag<Item>>().addComponents(ItemTags.getCollection().getIDTagMap()
                        .values(), x -> new TextBuilder().stack(new ItemStack(x.getAllElements().get(0))).text(ItemTags.getCollection().getDirectIdFromTag(x).toString()).build()));
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
                    GuiComboBoxMapped<ITag<Item>> box = (GuiComboBoxMapped<ITag<Item>>) gui.get("tag");
                    box.setLines(new TextMapBuilder<ITag<Item>>().setFilter(x -> x.toLowerCase().contains(((GuiTextfield) event.control).getText()))
                            .addComponents(ItemTags.getCollection().getIDTagMap().values(), x -> new TextBuilder().stack(new ItemStack(x.getAllElements().get(0)))
                                    .text(ItemTags.getCollection().getDirectIdFromTag(x).toString()).build()));
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
                gui.add(new GuiLabel("info", 5, 30).setTitle(new StringTextComponent("Nothing to select")));
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
