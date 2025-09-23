package team.creative.creativecore.client.gui.control.collection;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.gui.control.simple.GuiClientLabel;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector.GuiStackSelectorDist;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelector.StackCollector;
import team.creative.creativecore.common.gui.control.collection.GuiStackSelectorExtension;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextBuilder;
import team.creative.creativecore.common.util.type.map.HashMapList;

public class GuiClientStackSelector<T extends GuiStackSelector> extends GuiClientLabel<T> implements GuiStackSelectorDist {
    
    protected GuiExtensionCreator<GuiClientStackSelector, GuiStackSelectorExtension> ex = new GuiExtensionCreator<>(this);
    protected StackCollector collector;
    protected HashMapList<String, ItemStack> stacks;
    protected ItemStack selected = ItemStack.EMPTY;
    
    private boolean searchbar;
    
    public GuiClientStackSelector(T control) {
        super(control);
    }
    
    @Override
    public void setCollector(StackCollector collector) {
        this.collector = collector;
    }
    
    @Override
    public boolean hasSearchbar() {
        return searchbar;
    }
    
    @Override
    public void setSearchbar(boolean searchbar) {
        this.searchbar = searchbar;
    }
    
    @Override
    public boolean selectFirst(boolean notify) {
        if (stacks != null) {
            ItemStack first = stacks.getFirst();
            if (first != null) {
                setSelected(first, notify);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void updateCollectedStacks(boolean notify) {
        stacks = collector.collect(control.getPlayer());
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public boolean setSelectedForce(ItemStack stack, boolean notify) {
        setTitle(new TextBuilder().stack(stack).add(stack.getHoverName()).build());
        this.selected = stack;
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
        return true;
    }
    
    @Override
    public boolean setSelected(ItemStack stack, boolean notify) {
        if (stacks.contains(stack)) {
            setTitle(new TextBuilder().stack(stack).add(stack.getHoverName()).build());
            this.selected = stack;
            if (notify)
                raiseEvent(new GuiControlChangedEvent(control));
            return true;
        }
        return false;
    }
    
    @Override
    public HashMapList<String, ItemStack> getStacks() {
        return stacks;
    }
    
    @Override
    public ItemStack getSelected() {
        return selected;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        ex.toggle(this::createBox);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    protected GuiStackSelectorExtension createBox(GuiExtensionCreator<GuiClientStackSelector, GuiStackSelectorExtension> creator) {
        var ex = new GuiStackSelectorExtension(control.getParent(), control.name + "extension");
        ((GuiClientStackSelectorExtension) ex.dist()).creator = creator;
        return ex;
    }
    
    @Override
    public void looseFocus() {
        if (ex.checkShouldClose())
            ex.close();
    }
    
}
