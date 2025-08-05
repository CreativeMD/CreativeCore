package team.creative.creativecore.common.gui.control.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;

public class GuiSlot extends GuiSlotBase {
    
    public final Slot slot;
    
    private ItemStack lastSend = null;
    private boolean changed = false;
    
    public GuiSlot(IGuiParent parent, Container container, int index) {
        this(parent, "", container, index);
    }
    
    public GuiSlot(IGuiParent parent, String name, Container container, int index) {
        this(parent, name + index, new Slot(container, index, 0, 0));
    }
    
    public GuiSlot(IGuiParent parent, Slot slot) {
        this(parent, "" + slot.getContainerSlot(), slot);
    }
    
    public GuiSlot(IGuiParent parent, String name, Slot slot) {
        super(parent, name);
        this.slot = slot;
    }
    
    @Override
    public GuiSlotDist dist() {
        return (GuiSlotDist) super.dist();
    }
    
    @Override
    public ItemStack getStack() {
        return slot.getItem();
    }
    
    public GuiManagerItem itemManager() {
        return getLayer().itemManager();
    }
    
    public IGuiInventory inventory() {
        IGuiParent parent = getParent();
        while (!(parent instanceof IGuiInventory))
            if (parent instanceof GuiParent)
                parent = ((GuiParent) parent).getParent();
            else
                throw new RuntimeException("Slot needs inventory parent");
        return (IGuiInventory) parent;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!changed && (lastSend == null || !ItemStack.matches(slot.getItem(), lastSend)))
            changed();
    }
    
    public void onSendUpdate() {
        changed = false;
        lastSend = slot.getItem().copy();
    }
    
    public void changed() {
        changed = true;
        inventory().setChanged(slot.getContainerSlot());
        if (dist() != null)
            dist().changed();
    }
    
    public ItemStack insert(ItemStack stack) {
        boolean canStack = AbstractContainerMenu.canItemQuickReplace(slot, stack, true);
        int availableStackSize = slot.getMaxStackSize(stack);
        
        if (canStack && slot.mayPlace(stack)) {
            int neededStackSize = stack.getCount();
            if (slot.hasItem())
                availableStackSize -= slot.getItem().getCount();
            
            int additionalStackSize = Math.min(availableStackSize, neededStackSize);
            
            if (additionalStackSize > 0) {
                ItemStack inSlot = stack.copy();
                inSlot.setCount(additionalStackSize);
                if (slot.hasItem())
                    inSlot.grow(slot.getItem().getCount());
                slot.set(inSlot);
                stack.shrink(additionalStackSize);
                changed();
            }
        }
        return stack;
    }
    
    public static interface GuiSlotDist extends GuiControlDistHandler {
        
        public void changed();
        
    }
    
}
