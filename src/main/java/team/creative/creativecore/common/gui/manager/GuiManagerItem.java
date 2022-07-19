package team.creative.creativecore.common.gui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.inventory.GuiSlot;
import team.creative.creativecore.common.gui.controls.inventory.IGuiInventory;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobal;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiManagerItem extends GuiManager {
    
    private static final GuiSyncGlobal<GuiLayer, CompoundTag> HAND = GuiSyncHolder.GLOBAL.register("hand", (c, t) -> c.itemManager().setHand(ItemStack.of(t)));
    
    public static final GuiSyncGlobal<GuiSlot, ByteTag> DROP = GuiSyncHolder.GLOBAL.register("drop", (c, t) -> {
        boolean ctrl = t.getAsByte() == 1;
        Slot slot = c.slot;
        Player player = c.getPlayer();
        if (slot.hasItem() && slot.mayPickup(player)) {
            ItemStack drop = slot.getItem().copy();
            if (ctrl)
                slot.set(ItemStack.EMPTY);
            else
                drop.setCount(1);
            ItemStack newStack = slot.getItem();
            newStack.shrink(1);
            
            player.drop(drop, true);
            slot.onTake(player, drop);
            c.changed();
        }
    });
    
    public static final GuiSyncGlobal<GuiLayer, EndTag> DROP_HAND = GuiSyncHolder.GLOBAL.register("drop_hand", (c, t) -> {
        ItemStack hand = c.itemManager().getHand();
        Player player = c.getPlayer();
        if (!hand.isEmpty()) {
            player.drop(hand, true);
            c.itemManager().setHand(ItemStack.EMPTY);
        }
    });
    
    public static final GuiSyncGlobal<GuiSlot, IntTag> INSERT = GuiSyncHolder.GLOBAL.register("insert", (c, t) -> {
        Slot slot = c.slot;
        GuiLayer layer = c.getLayer();
        
        ItemStack stack = slot.getItem();
        int amount = Math.min(t.getAsInt(), stack.getCount());
        ItemStack insert = stack.copy();
        insert.setCount(amount);
        stack.shrink(amount);
        
        for (IGuiInventory inv : layer.inventoriesToInsert()) {
            if (inv == c.inventory())
                continue;
            
            inv.insert(insert, false);
            if (!insert.isEmpty())
                inv.insert(insert, true);
            
            if (insert.isEmpty())
                break;
        }
        
        stack.grow(insert.getCount());
        c.changed();
    });
    
    public static final GuiSyncGlobal<GuiSlot, IntTag> EXTRACT = GuiSyncHolder.GLOBAL.register("exract", (c, t) -> {
        Slot slot = c.slot;
        GuiLayer layer = c.getLayer();
        
        ItemStack stack = slot.getItem();
        
        if (stack.isEmpty())
            return;
        
        int amount = Math.min(t.getAsInt(), slot.getMaxStackSize(stack) - stack.getCount());
        ItemStack extract = stack.copy();
        extract.setCount(amount);
        
        for (IGuiInventory inv : layer.inventoriesToExract()) {
            if (inv == c.inventory())
                continue;
            
            inv.extract(extract);
            if (extract.isEmpty())
                break;
        }
        
        stack.grow(amount - extract.getCount());
        c.changed();
    });
    
    public static final GuiSyncGlobal<GuiSlot, EndTag> DUPLICATE = GuiSyncHolder.GLOBAL.register("duplicate", (c, t) -> {
        Slot slot = c.slot;
        Player player = c.getPlayer();
        
        if (player.isCreative()) {
            ItemStack stack = slot.getItem().copy();
            stack.setCount(stack.getMaxStackSize());
            c.itemManager().setHand(stack);
        }
    });
    
    public static final GuiSyncGlobal<GuiSlot, ByteTag> SWAP = GuiSyncHolder.GLOBAL.register("swap", (c, t) -> {
        boolean rightClick = t.getAsByte() == 1;
        Slot slot = c.slot;
        Player player = c.getPlayer();
        ItemStack hand = c.itemManager().getHand();
        if (slot.mayPickup(player) && (hand.isEmpty() || slot.mayPlace(hand))) {
            ItemStack pickup;
            if (hand.isEmpty() && rightClick)
                pickup = slot.getItem().split(Math.max(1, slot.getItem().getCount() / 2));
            else {
                pickup = slot.getItem().copy();
                slot.set(hand.copy());
            }
            slot.onTake(player, pickup);
            c.itemManager().setHand(pickup);
            
            c.changed();
        }
    });
    
    public static final GuiSyncGlobal<GuiLayer, CompoundTag> SPREAD = GuiSyncHolder.GLOBAL.register("spread", (c, t) -> {
        ItemStack hand = c.itemManager().getHand().copy();
        Player player = c.getPlayer();
        
        if (hand.isEmpty())
            return;
        
        List<IGuiInventory> inventories = new ArrayList<>();
        ListTag names = t.getList("names", Tag.TAG_STRING);
        for (int i = 0; i < names.size(); i++)
            inventories.add((IGuiInventory) c.get(names.getString(i)));
        
        List<GuiSlot> slots = new ArrayList<>();
        int[] ids = t.getIntArray("ids");
        for (int i = 0; i < ids.length; i += 2)
            slots.add(inventories.get(ids[i]).getSlot(ids[i + 1]));
        
        int countPerSlot = Math.max(1, Mth.floor((float) hand.getCount() / (float) slots.size()));
        boolean rightClick = t.getBoolean("rightClick");
        if (rightClick)
            countPerSlot = 1;
        for (GuiSlot slot : slots) {
            
            if (!slot.slot.mayPlace(hand))
                continue;
            
            ItemStack stack = slot.getStack();
            int transfer = Math.min(countPerSlot, slot.slot.getMaxStackSize(hand) - slot.getStack().getCount());
            ItemStack toTransfer = hand.copy();
            toTransfer.setCount(transfer);
            if (transfer <= 0)
                continue;
            
            if (!toTransfer.overrideStackedOnOther(slot.slot, rightClick ? ClickAction.SECONDARY : ClickAction.PRIMARY, player) && !stack
                    .overrideOtherStackedOnMe(toTransfer, slot.slot, rightClick ? ClickAction.SECONDARY : ClickAction.PRIMARY, player, c.itemManager().handAccess)) {
                if (!slot.slot.hasItem())
                    slot.slot.set(toTransfer);
                else if (ItemStack.isSameItemSameTags(toTransfer, stack))
                    stack.grow(transfer);
                else
                    continue;
                hand.shrink(transfer);
            } else
                hand = c.itemManager().getHand();
            
            slot.changed();
        }
        c.itemManager().setHand(hand);
    });
    
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
    public void renderOverlay(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
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
            matrix.pushPose();
            RenderSystem.disableScissor();
            
            matrix.translate(mouseX - 8, mouseY - 8, 200);
            GuiRenderHelper.drawItemStack(matrix, stack, 1);
            GuiRenderHelper.drawItemStackDecorations(matrix, stack, count);
            
            matrix.popPose();
        }
    }
    
    @Override
    public void tick() {
        if (handChanged) {
            handChanged = false;
            if (!layer.isClient())
                HAND.send(layer, hand.save(new CompoundTag()));
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
            DROP_HAND.send(layer, EndTag.INSTANCE);
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
            ids[i + 1] = slot.slot.getSlotIndex();
            i += 2;
        }
        nbt.putIntArray("ids", ids);
        nbt.put("names", names);
        SPREAD.sendAndExecute(layer, nbt);
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
