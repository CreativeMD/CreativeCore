package team.creative.creativecore;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.inventory.GuiSlot;
import team.creative.creativecore.common.gui.controls.inventory.IGuiInventory;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobal;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class CreativeCoreGuiRegistry {
    
    public static final GuiSyncGlobal<GuiLayer, CompoundTag> HAND = GuiSyncHolder.GLOBAL.register("hand", (c, t) -> c.itemManager().setHand(ItemStack.of(t)));
    
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
        ItemStack insert = slot.remove(amount);
        
        for (IGuiInventory inv : layer.inventoriesToInsert()) {
            if (inv == c.inventory())
                continue;
            inv.insertClever(insert);
            if (insert.isEmpty())
                break;
        }
        
        if (insert.getCount() == amount) // Nothing has happened try to move it around inside the inventory. Used by the player inventory
            insert = c.inventory().moveInside(insert, slot.getContainerSlot());
        
        stack.grow(insert.getCount());
        c.changed();
    });
    
    public static final GuiSyncGlobal<GuiSlot, IntTag> EXTRACT = GuiSyncHolder.GLOBAL.register("exract", (c, t) -> {
        Slot slot = c.slot;
        GuiLayer layer = c.getLayer();
        
        ItemStack stack = slot.getItem();
        
        if (stack.isEmpty() || !slot.mayPlace(stack))
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
                pickup = slot.remove(Math.max(1, slot.getItem().getCount() / 2));
            else
                pickup = slot.remove(slot.getItem().getCount());
            slot.onTake(player, pickup);
            if (!hand.isEmpty())
                slot.set(hand);
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
            inventories.add(c.get(names.getString(i)));
        
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
            
            if (!toTransfer.overrideStackedOnOther(slot.slot, rightClick ? ClickAction.SECONDARY : ClickAction.PRIMARY, player) && !stack.overrideOtherStackedOnMe(toTransfer,
                slot.slot, rightClick ? ClickAction.SECONDARY : ClickAction.PRIMARY, player, c.itemManager().handAccess)) {
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
    
    public static void init() {}
    
}
