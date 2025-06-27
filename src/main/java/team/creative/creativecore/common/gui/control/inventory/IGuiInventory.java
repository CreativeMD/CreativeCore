package team.creative.creativecore.common.gui.control.inventory;

import java.util.BitSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobal;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public interface IGuiInventory {
    
    public static final GuiSyncGlobal<GuiControl, CompoundTag> SYNC = GuiSyncHolder.GLOBAL.register("inv_one", (control, nbt) -> {
        IGuiInventory inventory = (IGuiInventory) control;
        for (String name : nbt.keySet()) {
            int id = Integer.parseInt(name);
            inventory.getSlot(id).slot.set(nbt.read(name, ItemStack.OPTIONAL_CODEC).get());
            inventory.setChanged(id);
        }
    });
    
    public static final GuiSyncGlobal<GuiControl, ListTag> SYNC_ALL = GuiSyncHolder.GLOBAL.register("inv_all", (control, list) -> {
        IGuiInventory inventory = (IGuiInventory) control;
        for (int i = 0; i < inventory.inventorySize(); i++)
            inventory.getSlot(i).slot.set(ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, list.getCompoundOrEmpty(i)).getOrThrow());
        inventory.setChanged();
    });
    
    public GuiSlot getSlot(int index);
    
    public int inventorySize();
    
    public String name();
    
    public void setChanged();
    
    public void setChanged(int slotIndex);
    
    public default ItemStack moveInside(ItemStack toAdd, int slot) {
        return toAdd;
    }
    
    public default void insertClever(ItemStack toAdd) {
        insertClever(toAdd, 0, inventorySize());
    }
    
    public default void insertClever(ItemStack toAdd, int start, int endExclusive) {
        insert(toAdd, false, start, endExclusive);
        if (!toAdd.isEmpty())
            insert(toAdd, true, start, endExclusive);
    }
    
    public default void insert(ItemStack toAdd, boolean useEmptySlot) {
        insert(toAdd, useEmptySlot, 0, inventorySize());
    }
    
    public default void insert(ItemStack toAdd, boolean useEmptySlot, int start, int endExclusive) {
        for (int i = start; i < endExclusive; i++) {
            GuiSlot slot = getSlot(i);
            if (useEmptySlot || slot.slot.hasItem()) {
                toAdd = slot.insert(toAdd);
                if (toAdd.isEmpty())
                    return;
            }
        }
    }
    
    public default void extract(ItemStack toDrain) {
        Player player = ((GuiControl) this).getPlayer();
        for (int i = 0; i < inventorySize(); i++) {
            GuiSlot slot = getSlot(i);
            if (slot.slot.mayPickup(player) && ItemStack.isSameItemSameComponents(toDrain, slot.getStack())) {
                int transfer = Math.min(toDrain.getCount(), slot.getStack().getCount());
                if (transfer <= 0)
                    continue;
                
                ItemStack drained = slot.slot.remove(transfer);
                slot.slot.onTake(player, drained);
                
                toDrain.shrink(transfer);
                
                if (toDrain.isEmpty())
                    return;
            }
        }
    }
    
    public default void sync(BitSet set) {
        GuiControl control = (GuiControl) this;
        
        if (control.isClient())
            return;
        CompoundTag nbt = new CompoundTag();
        for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
            GuiSlot slot = getSlot(i);
            slot.onSendUpdate();
            nbt.store("" + i, ItemStack.OPTIONAL_CODEC, slot.slot.getItem());
        }
        SYNC.send(control, nbt);
    }
    
    public default void syncAll() {
        GuiControl control = (GuiControl) this;
        if (control.isClient())
            return;
        ListTag list = new ListTag();
        for (int i = 0; i < inventorySize(); i++)
            list.add(ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, getSlot(i).slot.getItem()).getOrThrow());
        SYNC_ALL.send(control, list);
    }
}
