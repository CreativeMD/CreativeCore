package team.creative.creativecore.common.gui.controls.collection;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.StackUtils;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.type.map.HashMapList;

import java.util.ArrayList;
import java.util.List;

public class GuiStackSelector extends GuiLabel {
    
    protected GuiStackSelectorExtension extension;
    public StackCollector collector;
    protected HashMapList<String, ItemStack> stacks;
    public boolean extensionLostFocus;
    
    public Player player;
    public boolean searchBar;
    
    public GuiStackSelector(String name, Player player, StackCollector collector, boolean searchBar) {
        super(name);
        this.searchBar = searchBar;
        this.player = player;
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
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (extension == null)
            openBox(rect);
        else
            closeBox();
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
    
    protected ItemStack selected = ItemStack.EMPTY;
    
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
    
    public void openBox(Rect rect) {
        this.extension = createBox();
        GuiChildControl child = getLayer().addHover(extension);
        
        rect = toLayerRect(new Rect(0, 0, rect.getWidth(), rect.getHeight()));
        extension.init();
        child.setX((int) rect.minX);
        child.setY((int) rect.maxY);
        
        child.setWidth((int) rect.getWidth(), (int) getLayer().rect.getWidth() - getContentOffset() * 2);
        child.flowX();
        int height = (int) getLayer().rect.getHeight() - getContentOffset() * 2;
        child.setHeight(child.getPreferredHeight(height), height);
        child.flowY();
        
        Rect absolute = toScreenRect(child.rect.copy());
        Rect screen = Rect.getScreenRect();
        
        if (absolute.maxY > screen.maxY && absolute.minY - absolute.getHeight() >= screen.minX)
            child.setY(child.getY() - ((int) rect.getHeight() + child.getHeight()));
    }
    
    public void closeBox() {
        if (extension != null) {
            ((GuiParent) getLayer()).remove(extension);
            extension = null;
        }
    }
    
    protected GuiStackSelectorExtension createBox() {
        return new GuiStackSelectorExtension(name + "extension", getPlayer(), this);
    }
    
    public boolean select(String line) {
        return false;
    }
    
    @Override
    public void looseFocus() {
        if (extensionLostFocus && extension != null)
            closeBox();
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
                        LazyOptional<IItemHandler> result = StackUtils.getStackInventory(stack);
                        if (result.isPresent())
                            collect(result.orElseThrow(RuntimeException::new), tempStacks);
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
                    LazyOptional<IItemHandler> result = StackUtils.getStackInventory(stack);
                    if (result.isPresent())
                        collect(result.orElseThrow(RuntimeException::new), stacks);
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
            for (Item item : Registry.ITEM) {
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
            if (super.allow(stack))
                return Block.byItem(stack.getItem()) != null && !(Block.byItem(stack.getItem()) instanceof AirBlock);
            return false;
        }
        
    }
    
    public static boolean contains(String search, ItemStack stack) {
        if (search.equals(""))
            return true;
        if (getItemName(stack).toLowerCase().contains(search))
            return true;
        for (Component line : stack.getTooltipLines(null, TooltipFlag.Default.NORMAL))
            if (line.getString().toLowerCase().contains(search))
                return true;
            
        return false;
    }
    
    public static String getItemName(ItemStack stack) {
        String itemName = "";
        try {
            itemName = stack.getDisplayName().getString();
        } catch (Exception e) {
            itemName = Registry.ITEM.getKey(stack.getItem()).toString();
        }
        return itemName;
    }
    
}
