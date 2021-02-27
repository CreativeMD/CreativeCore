package team.creative.creativecore.common.util.ingredient;

import java.util.Iterator;
import java.util.Set;

import com.creativemd.creativecore.common.gui.controls.gui.custom.GuiStackSelectorAll;
import com.creativemd.creativecore.common.gui.controls.gui.custom.GuiStackSelectorAll.SearchSelector;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.FullItemDialogGuiLayer;
import team.creative.creativecore.common.gui.controls.GuiLabel;
import team.creative.creativecore.common.gui.controls.GuiStateButton;
import team.creative.creativecore.common.gui.controls.GuiTextfield;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
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
                GuiStackSelectorAll selector = new GuiStackSelectorAll("inv", 0, 30, 122, null, new GuiStackSelectorAll.CreativeCollector(new GuiStackSelectorAll.SearchSelector()));
                gui.add(selector);
                gui.add(new GuiTextfield("search", "", 0, 57, 144, 14));
                
                gui.add(new GuiLabel("guilabel1", 0, 80));
                gui.add(new GuiLabel("guilabel2", 0, 90));
                
                GuiStateButton damage = new GuiStateButton("damage", 0, 0, 106, 70, 14, "Damage: Off", "Damage: On");
                gui.add(damage);
                GuiStateButton nbt = new GuiStateButton("nbt", 0, 80, 106, 60, 14, "NBT: Off", "NBT: On");
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
                ItemStack stack = ((GuiStackSelectorAll) gui.get("inv")).getSelected();
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
                if (event.source.is("search")) {
                    GuiStackSelectorAll inv = (GuiStackSelectorAll) gui.get("inv");
                    ((SearchSelector) inv.collector.selector).search = ((GuiTextfield) event.source).text.toLowerCase();
                    inv.updateCollectedStacks();
                    inv.closeBox();
                } else if (event.source.is("inv")) {
                    GuiStackSelectorAll selector = (GuiStackSelectorAll) gui.get("inv");
                    if (selector != null) {
                        ItemStack stack = selector.getSelected();
                        if (!stack.isEmpty()) {
                            
                            ((GuiLabel) gui.get("guilabel1")).setCaption("damage: " + stack.getItemDamage());
                            ((GuiLabel) gui.get("guilabel2")).setCaption("nbt: " + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "null"));
                        } else {
                            ((GuiLabel) gui.get("guilabel1")).setCaption("");
                            ((GuiLabel) gui.get("guilabel2")).setCaption("");
                        }
                    }
                }
            }
        };
        registerGuiInfoHandler("Default", defaultHandler);
        
        registerGuiInfoHandler("Material", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                ItemStack blockStack = ((GuiStackSelectorAll) gui.get("inv")).getSelected();
                if (blockStack != null) {
                    Block block = Block.getBlockFromItem(blockStack.getItem());
                    if (!(block instanceof AirBlock))
                        return new CreativeIngredientMaterial(block.getDefaultState().getMaterial());
                }
                return null;
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                GuiStackSelectorAll selector = new GuiStackSelectorAll("inv", 0, 30, 122, null, new GuiStackSelectorAll.CreativeCollector(new GuiStackSelectorAll.GuiBlockSelector()));
                gui.add(selector);
                if (info instanceof CreativeIngredientMaterial)
                    selector.setSelectedForce(info.getExample());
            }
            
            @Override
            public boolean canHandle(CreativeIngredient info) {
                return info instanceof CreativeIngredientMaterial;
            }
        });
        
        registerGuiInfoHandler("Fuel", new GuiCreativeIngredientHandler() {
            
            @Override
            public CreativeIngredient parseInfo(FullItemDialogGuiLayer gui) {
                return new CreativeIngredientFuel();
            }
            
            @Override
            public void createControls(FullItemDialogGuiLayer gui, CreativeIngredient info) {
                gui.add(new GuiLabel("Nothing to select", 5, 30));
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
