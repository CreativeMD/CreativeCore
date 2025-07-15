package team.creative.creativecore.client.gui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCoreGuiRegistry;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.inventory.GuiSlot;
import team.creative.creativecore.common.gui.manager.GuiManagerItem;

public class GuiClientManagerItem extends GuiClientManager<GuiManagerItem> {
    
    public static int freeSpace(Slot slot, ItemStack hand) {
        if (slot.mayPlace(hand) && AbstractContainerMenu.canItemQuickReplace(slot, hand, true)) {
            int stackSize = slot.getMaxStackSize(hand);
            if (slot.hasItem())
                stackSize -= slot.getItem().getCount();
            return stackSize;
        }
        return -1;
    }
    
    private boolean drag;
    private boolean rightClick;
    private List<GuiSlot> dragged;
    private List<Integer> stackSizes;
    
    public GuiClientManagerItem(GuiManagerItem manager) {
        super(manager);
    }
    
    @Override
    public void mouseClickedOutside(double x, double y) {
        if (!manager.getHand().isEmpty())
            CreativeCoreGuiRegistry.DROP_HAND.send(manager.layer, EndTag.INSTANCE);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        if (isDragged() && rightClick == (button == 1))
            endDrag();
    }
    
    @Override
    public void renderOverlay(GuiGraphics graphics, GuiClientLayer layer, int mouseX, int mouseY) {
        ItemStack stack = manager.getHand();
        int count = stack.getCount();
        if (drag) {
            int used = 0;
            int countPerSlot = Math.max(1, Mth.floor((float) manager.getHand().getCount() / stackSizes.size()));
            if (rightClick)
                countPerSlot = 1;
            for (int i = 0; i < stackSizes.size(); i++)
                used += Math.min(countPerSlot, stackSizes.get(i));
            int left = manager.getHand().getCount() - used;
            count = Math.max(0, left);
        }
        
        if (!stack.isEmpty() && (!drag || rightClick || dragged.size() > 1)) {
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            RenderSystem.disableScissorForRenderTypeDraws();
            
            pose.translate(mouseX - 8, mouseY - 8);
            graphics.renderItem(stack, 0, 0);
            ((CreativeGuiGraphics) graphics).renderItemDecorations(stack, 0, 0, count == 1 ? null : "" + count);
            pose.popMatrix();
        }
    }
    
    public void startDrag(GuiSlot slot, boolean rightClick, int stackSize) {
        drag = true;
        dragged = new ArrayList<>();
        dragged.add(slot);
        this.rightClick = rightClick;
        stackSizes = new ArrayList<>();
        stackSizes.add(stackSize);
        slot.draggedIndex = 0;
    }
    
    public void addToDrag(GuiSlot slot) {
        int stackSize = freeSpace(slot.slot, manager.getHand());
        if (stackSize > 0) {
            slot.draggedIndex = dragged.size();
            dragged.add(slot);
            stackSizes.add(stackSize);
        }
    }
    
    public void modifyDrag(GuiSlot slot) {
        stackSizes.set(slot.draggedIndex, freeSpace(slot.slot, manager.getHand()));
    }
    
    public void abortDrag() {
        for (GuiSlot slot : dragged)
            slot.draggedIndex = -1;
        drag = false;
        rightClick = false;
        dragged = null;
        stackSizes = null;
    }
    
    public void endDrag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("rightClick", rightClick);
        
        HashMap<String, Integer> inventories = new HashMap<>();
        ListTag names = new ListTag();
        int[] ids = new int[dragged.size() * 2];
        int i = 0;
        for (GuiSlot slot : dragged) {
            String inventory = slot.inventory().name();
            Integer inventoryId = inventories.get(inventory);
            if (inventoryId == null) {
                inventories.put(inventory, inventoryId = inventories.size());
                names.add(StringTag.valueOf(inventory));
            }
            ids[i] = inventoryId;
            ids[i + 1] = slot.slot.getContainerSlot();
            i += 2;
        }
        nbt.putIntArray("ids", ids);
        nbt.put("names", names);
        CreativeCoreGuiRegistry.SPREAD.sendAndExecute(manager.layer, nbt);
        if (isDragged())
            abortDrag();
    }
    
    public boolean isDragged() {
        return drag;
    }
    
    public void setHand(ItemStack stack) {
        manager.setHand(stack);
    }
    
    public int additionalDragCount(int index) {
        if (index >= manager.getHand().getCount())
            return 0;
        
        if (rightClick)
            return 1;
        
        return Math.max(1, Mth.floor((float) manager.getHand().getCount() / dragged.size()));
    }
    
}
