package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.StackUtils;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiStackSelector extends GuiLabel {
    
    public final Player player;
    
    protected GuiExtensionCreator<GuiStackSelector, GuiStackSelectorExtension> ex = new GuiExtensionCreator<>(this);
    protected StackCollector collector;
    protected HashMapList<String, ItemStack> stacks;
    protected ItemStack selected = ItemStack.EMPTY;
    
    private boolean searchbar;
    
    public GuiStackSelector(String name, Player player, StackCollector collector, boolean searchbar) {
        super(name);
        this.player = player;
        this.searchbar = searchbar;
        this.collector = collector;
        updateCollectedStacks();
        selectFirst();
        setAlign(Align.CENTER);
    }
    
    public GuiStackSelector(String name, Player player, StackCollector collector) {
        this(name, player, collector, true);
    }
    
    public GuiStackSelector setWidth(int width) {
        setDim(width, 14);
        return this;
    }
    
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    public GuiStackSelector setSearchbar(boolean searchbar) {
        this.searchbar = searchbar;
        return this;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        ex.toggle(this::createBox, rect);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    public boolean selectFirst() {
        if (stacks != null) {
            ItemStack first = stacks.getFirst();
            if (first != null) {
                setSelected(first);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    public void updateCollectedStacks() {
        stacks = collector.collect(player);
    }
    
    public boolean setSelectedForce(ItemStack stack) {
        setTitle(new TextBuilder().stack(stack).add(stack.getHoverName()).build());
        this.selected = stack;
        raiseEvent(new GuiControlChangedEvent(this));
        return true;
    }
    
    public boolean setSelected(ItemStack stack) {
        if (stacks.contains(stack)) {
            setTitle(new TextBuilder().stack(stack).add(stack.getHoverName()).build());
            this.selected = stack;
            raiseEvent(new GuiControlChangedEvent(this));
            return true;
        }
        return false;
    }
    
    public HashMapList<String, ItemStack> getStacks() {
        return stacks;
    }
    
    public ItemStack getSelected() {
        return selected;
    }
    
    protected GuiStackSelectorExtension createBox(GuiExtensionCreator<GuiStackSelector, GuiStackSelectorExtension> creator) {
        return new GuiStackSelectorExtension(name + "extension", getPlayer(), creator);
    }
    
    @Override
    public void looseFocus() {
        if (ex.shouldClose())
            ex.close();
    }
    
    public static abstract class StackCollector {
        
        public StackSelector selector;
        
        public StackCollector(StackSelector selector) {
            this.selector = selector;
        }
        
        public abstract HashMapList<String, ItemStack> collect(Player player);
        
    }
    
    public static class InventoryCollector extends StackCollector {
        
        public InventoryCollector(StackSelector selector) {
            super(selector);
        }
        
        @Override
        public HashMapList<String, ItemStack> collect(Player player) {
            HashMapList<String, ItemStack> stacks = new HashMapList<>();
            
            if (player != null) {
                // Inventory
                List<ItemStack> tempStacks = new ArrayList<>();
                for (ItemStack stack : player.inventoryMenu.getItems())
                    if (!stack.isEmpty() && selector.allow(stack))
                        tempStacks.add(stack.copy());
                    else {
                        IItemHandler result = StackUtils.getStackInventory(stack);
                        if (result != null)
                            collect(result, tempStacks);
                    }
                
                stacks.add("collector.inventory", tempStacks);
            }
            
            return stacks;
        }
        
        protected void collect(IItemHandler inventory, List<ItemStack> stacks) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && selector.allow(stack))
                    stacks.add(stack.copy());
                else {
                    IItemHandler result = StackUtils.getStackInventory(stack);
                    if (result != null)
                        collect(result, stacks);
                }
                
            }
            
        }
    }
    
    public static class CreativeCollector extends InventoryCollector {
        
        public CreativeCollector(StackSelector selector) {
            super(selector);
        }
        
        @Override
        public HashMapList<String, ItemStack> collect(Player player) {
            HashMapList<String, ItemStack> stacks = super.collect(player);
            
            List<ItemStack> newStacks = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                ItemStack stack = new ItemStack(item);
                if (selector.allow(stack))
                    newStacks.add(stack);
            }
            
            stacks.add("collector.all", newStacks);
            
            return stacks;
        }
    }
    
    public static abstract class StackSelector {
        
        public abstract boolean allow(ItemStack stack);
        
    }
    
    public static class SearchSelector extends StackSelector {
        
        public String search = "";
        
        @Override
        public boolean allow(ItemStack stack) {
            return contains(search, stack);
        }
        
    }
    
    public static class GuiBlockSelector extends SearchSelector {
        
        @Override
        public boolean allow(ItemStack stack) {
            if (super.allow(stack)) {
                return !(Block.byItem(stack.getItem()) instanceof AirBlock);
            }
            return false;
        }
        
    }
    
    public static boolean contains(String search, ItemStack stack) {
        if (search.isEmpty())
            return true;
        if (getItemName(stack).toLowerCase().contains(search))
            return true;
        for (Component line : stack.getTooltipLines(TooltipContext.EMPTY, null, TooltipFlag.Default.NORMAL))
            if (line.getString().toLowerCase().contains(search))
                return true;
            
        return false;
    }
    
    public static String getItemName(ItemStack stack) {
        String itemName = "";
        try {
            itemName = stack.getDisplayName().getString();
        } catch (Exception e) {
            itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        }
        return itemName;
    }
    
}
