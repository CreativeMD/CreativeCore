package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.util.mc.StackUtils;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiStackSelector extends GuiLabel {
    
    public GuiStackSelector(IGuiParent parent, String name, StackCollector collector, boolean searchbar) {
        super(parent, name);
        if (dist() != null) {
            dist().setSearchbar(searchbar);
            dist().setCollector(collector);
            dist().updateCollectedStacks(false);
            dist().selectFirst(false);
        }
        setAlign(Align.CENTER);
    }
    
    public GuiStackSelector(IGuiParent parent, String name, StackCollector collector) {
        this(parent, name, collector, true);
    }
    
    @Override
    public GuiStackSelectorDist dist() {
        return (GuiStackSelectorDist) super.dist();
    }
    
    public GuiStackSelector setWidth(int width) {
        setDim(width, 14);
        return this;
    }
    
    public boolean hasSearchbar() {
        if (dist() != null)
            return dist().hasSearchbar();
        return false;
    }
    
    public GuiStackSelector setSearchbar(boolean searchbar) {
        if (dist() != null)
            dist().setSearchbar(searchbar);
        return this;
    }
    
    public boolean selectFirst() {
        if (dist() != null)
            return dist().selectFirst(true);
        return false;
    }
    
    public void updateCollectedStacks() {
        if (dist() != null)
            dist().updateCollectedStacks(true);
    }
    
    public boolean setSelectedForce(ItemStack stack) {
        if (dist() != null)
            return dist().setSelectedForce(stack, true);
        return false;
    }
    
    public boolean setSelected(ItemStack stack) {
        if (dist() != null)
            return dist().setSelected(stack, true);
        return false;
    }
    
    public HashMapList<String, ItemStack> getStacks() {
        if (dist() != null)
            return dist().getStacks();
        return null;
    }
    
    public ItemStack getSelected() {
        if (dist() != null)
            return dist().getSelected();
        return ItemStack.EMPTY;
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
                    else
                        StackUtils.collect(stack, selector::allow, tempStacks);
                    
                stacks.add("collector.inventory", tempStacks);
            }
            
            return stacks;
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
    
    public static interface GuiStackSelectorDist extends GuiLabelDist {
        
        public void setCollector(StackCollector collector);
        
        public boolean hasSearchbar();
        
        public void setSearchbar(boolean searchbar);
        
        public boolean selectFirst(boolean notify);
        
        public void updateCollectedStacks(boolean notify);
        
        public boolean setSelectedForce(ItemStack stack, boolean notify);
        
        public boolean setSelected(ItemStack stack, boolean notify);
        
        public HashMapList<String, ItemStack> getStacks();
        
        public ItemStack getSelected();
        
    }
    
}
