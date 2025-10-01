package team.creative.creativecore.client.gui.control.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCoreGuiRegistry;
import team.creative.creativecore.client.gui.manager.GuiClientManagerItem;
import team.creative.creativecore.common.gui.control.inventory.GuiSlot;
import team.creative.creativecore.common.gui.control.inventory.GuiSlot.GuiSlotDist;
import team.creative.creativecore.common.gui.control.inventory.IGuiInventory;

public class GuiClientSlot<T extends GuiSlot> extends GuiClientSlotBase<T> implements GuiSlotDist {
    
    public int draggedIndex = -1;
    
    public GuiClientSlot(T control) {
        super(control);
    }
    
    public GuiClientManagerItem itemManager() {
        return (GuiClientManagerItem) control.itemManager().dist;
    }
    
    public IGuiInventory inventory() {
        return control.inventory();
    }
    
    @Override
    public void changed() {
        if (draggedIndex != -1)
            itemManager().modifyDrag(this);
    }
    
    @Override
    protected ItemStack getStackToRender() {
        if (draggedIndex != -1) {
            ItemStack stack = itemManager().getHand().copy();
            int toAdd = Math.min(itemManager().additionalDragCount(draggedIndex), control.slot.getMaxStackSize(stack) - control.slot.getItem().getCount());
            stack.setCount(toAdd + control.slot.getItem().getCount());
            return stack;
        }
        return control.getStack();
    }
    
    @Override
    public boolean keyPressed(KeyEvent key) {
        if (Minecraft.getInstance().options.keyDrop.matches(key) && isHovered()) {
            CreativeCoreGuiRegistry.DROP.sendAndExecute(control, ByteTag.valueOf(key.hasControlDown()));
            return true;
        }
        return super.keyPressed(key);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (!Minecraft.getInstance().hasShiftDown())
            return false;
        
        if (delta > 0)
            CreativeCoreGuiRegistry.INSERT.sendAndExecute(control, IntTag.valueOf((int) delta));
        else
            CreativeCoreGuiRegistry.EXTRACT.sendAndExecute(control, IntTag.valueOf((int) -delta));
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        if (itemManager().isDragged())
            return true;
        
        if (info.hasShiftDown()) {
            if (control.slot.mayPickup(control.getPlayer()))
                CreativeCoreGuiRegistry.INSERT.sendAndExecute(control, IntTag.valueOf(control.slot.getMaxStackSize()));
            return true;
        }
        
        ItemStack hand = itemManager().getHand();
        if (!hand.isEmpty() && info.button() < 2) {
            int stackSize = GuiClientManagerItem.freeSpace(control.slot, hand);
            if (stackSize > 0)
                itemManager().startDrag(this, info.button() == 1, stackSize);
            if (stackSize != -1)
                return true;
        }
        
        if (info.button() == 2)
            CreativeCoreGuiRegistry.DUPLICATE.sendAndExecute(control, EndTag.INSTANCE);
        else if (control.slot.mayPickup(control.getPlayer()) && (hand.isEmpty() || control.slot.mayPlace(hand)))
            CreativeCoreGuiRegistry.SWAP.sendAndExecute(control, ByteTag.valueOf(info.button() == 1));
        return true;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (draggedIndex == -1 && itemManager().isDragged() && rect.inside(x + rect.getX(), y + rect.getY()))
            itemManager().addToDrag(this);
    }
    
    public Slot slot() {
        return control.slot;
    }
    
}
