package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCoreGuiRegistry;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSlot extends GuiSlotBase {
    
    public final Slot slot;
    public int draggedIndex = -1;
    
    public GuiSlot(Container container, int index) {
        this("", container, index);
    }
    
    public GuiSlot(String name, Container container, int index) {
        this(name + index, new Slot(container, index, 0, 0));
    }
    
    public GuiSlot(Slot slot) {
        this("" + slot.getContainerSlot(), slot);
    }
    
    public GuiSlot(String name, Slot slot) {
        super(name);
        this.slot = slot;
    }
    
    @Override
    public ItemStack getStack() {
        return slot.getItem();
    }
    
    @Override
    protected ItemStack getStackToRender() {
        if (draggedIndex != -1) {
            ItemStack stack = itemManager().getHand().copy();
            int toAdd = Math.min(itemManager().additionalDragCount(draggedIndex), slot.getMaxStackSize(stack) - slot.getItem().getCount());
            stack.setCount(toAdd + slot.getItem().getCount());
            return stack;
        }
        return getStack();
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().options.keyDrop.matches(keyCode, scanCode)) {
            CreativeCoreGuiRegistry.DROP.sendAndExecute(this, ByteTag.valueOf(Screen.hasControlDown()));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
        if (!Screen.hasShiftDown())
            return false;
        
        if (delta > 0)
            CreativeCoreGuiRegistry.INSERT.sendAndExecute(this, IntTag.valueOf((int) delta));
        else
            CreativeCoreGuiRegistry.EXTRACT.sendAndExecute(this, IntTag.valueOf((int) delta));
        return true;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (itemManager().isDragged())
            return true;
        
        if (Screen.hasShiftDown()) {
            if (slot.mayPickup(getPlayer()))
                CreativeCoreGuiRegistry.INSERT.sendAndExecute(this, IntTag.valueOf(slot.getMaxStackSize()));
            return true;
        }
        
        ItemStack hand = itemManager().getHand();
        if (!hand.isEmpty() && button < 2) {
            int stackSize = GuiManagerItem.freeSpace(slot, hand);
            if (stackSize > 0)
                itemManager().startDrag(this, button == 1, stackSize);
            if (stackSize != -1)
                return true;
        }
        
        if (button == 2)
            CreativeCoreGuiRegistry.DUPLICATE.sendAndExecute(this, EndTag.INSTANCE);
        else if (slot.mayPickup(getPlayer()) && (hand.isEmpty() || slot.mayPlace(hand)))
            CreativeCoreGuiRegistry.SWAP.sendAndExecute(this, ByteTag.valueOf(button == 1));
        return true;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (draggedIndex == -1 && itemManager().isDragged() && rect.inside(x + rect.minX, y + rect.minY))
            itemManager().addToDrag(this);
    }
    
    public void changed() {
        inventory().setChanged(slot.getContainerSlot());
        if (draggedIndex != -1)
            itemManager().modifyDrag(this);
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
    
}
