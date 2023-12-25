package team.creative.creativecore.common.gui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCoreGuiRegistry;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.inventory.GuiSlot;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiManagerItem extends GuiManager {
    
    public static int freeSpace(Slot slot, ItemStack hand) {
        if (slot.mayPlace(hand) && AbstractContainerMenu.canItemQuickReplace(slot, hand, true)) {
            int stackSize = slot.getMaxStackSize(hand);
            if (slot.hasItem())
                stackSize -= slot.getItem().getCount();
            return stackSize;
        }
        return -1;
    }
    
    private ItemStack hand = ItemStack.EMPTY;
    private boolean drag;
    private boolean rightClick;
    private List<GuiSlot> dragged;
    private List<Integer> stackSizes;
    private boolean handChanged;
    public SlotAccess handAccess = new SlotAccess() {
        @Override
        public ItemStack get() {
            return getHand();
        }
        
        @Override
        public boolean set(ItemStack hand) {
            setHand(hand);
            return true;
        }
    };
    
    public GuiManagerItem(GuiLayer layer) {
        super(layer);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void renderOverlay(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        ItemStack stack = hand;
        int count = stack.getCount();
        if (drag) {
            int used = 0;
            int countPerSlot = Math.max(1, Mth.floor((float) hand.getCount() / (float) stackSizes.size()));
            if (rightClick)
                countPerSlot = 1;
            for (int i = 0; i < stackSizes.size(); i++)
                used += Math.min(countPerSlot, stackSizes.get(i));
            int left = hand.getCount() - used;
            count = Math.max(0, left);
        }
        
        if (!stack.isEmpty() && (!drag || rightClick || dragged.size() > 1)) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            RenderSystem.disableScissor();
            
            pose.translate(mouseX - 8, mouseY - 8, 200);
            GuiRenderHelper.drawItemStack(pose, stack, 1);
            GuiRenderHelper.drawItemStackDecorations(pose, stack, count);
            
            pose.popPose();
        }
    }
    
    @Override
    public void tick() {
        if (handChanged) {
            handChanged = false;
            if (!layer.isClient())
                CreativeCoreGuiRegistry.HAND.send(layer, hand.save(new CompoundTag()));
        }
        super.tick();
    }
    
    @Override
    public void closed() {
        if (!layer.isClient() && !hand.isEmpty() && !layer.getPlayer().addItem(hand))
            layer.getPlayer().drop(hand, false);
        super.closed();
    }
    
    @Override
    public void mouseClickedOutside(double x, double y) {
        if (!hand.isEmpty())
            CreativeCoreGuiRegistry.DROP_HAND.send(layer, EndTag.INSTANCE);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        if (isDragged() && rightClick == (button == 1))
            endDrag();
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
        int stackSize = freeSpace(slot.slot, hand);
        if (stackSize > 0) {
            slot.draggedIndex = dragged.size();
            dragged.add(slot);
            stackSizes.add(stackSize);
        }
    }
    
    public void modifyDrag(GuiSlot slot) {
        stackSizes.set(slot.draggedIndex, freeSpace(slot.slot, hand));
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
        CreativeCoreGuiRegistry.SPREAD.sendAndExecute(layer, nbt);
        if (isDragged())
            abortDrag();
    }
    
    public boolean isDragged() {
        return drag;
    }
    
    public ItemStack getHand() {
        return hand;
    }
    
    public void setHandChanged() {
        handChanged = true;
    }
    
    public void setHand(ItemStack stack) {
        if (isDragged())
            abortDrag();
        this.hand = stack;
        setHandChanged();
    }
    
    public int additionalDragCount(int index) {
        if (index >= hand.getCount())
            return 0;
        
        if (rightClick)
            return 1;
        
        return Math.max(1, Mth.floor(hand.getCount() / dragged.size()));
    }
    
}
