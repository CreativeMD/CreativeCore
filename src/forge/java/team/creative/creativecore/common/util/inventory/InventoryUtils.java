package team.creative.creativecore.common.util.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;

public class InventoryUtils {
    
    public static CompoundTag save(SimpleContainer basic) {
        CompoundTag nbt = new CompoundTag();
        for (int i = 0; i < basic.getContainerSize(); i++) {
            if (basic.getItem(i).isEmpty())
                continue;
            nbt.put("s" + i, basic.getItem(i).save(new CompoundTag()));
        }
        nbt.putInt("size", basic.getContainerSize());
        return nbt;
    }
    
    public static SimpleContainer load(CompoundTag nbt) {
        return load(nbt, nbt.getInt("size"));
    }
    
    public static SimpleContainer load(CompoundTag nbt, int length) {
        SimpleContainer basic = new SimpleContainer(length);
        for (int i = 0; i < length; i++) {
            if (nbt.contains("s" + i))
                basic.setItem(i, ItemStack.of(nbt.getCompound("s" + i)));
            else
                basic.setItem(i, ItemStack.EMPTY);
        }
        return basic;
    }
    
    public static boolean isItemStackEqual(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty())
            return true;
        
        if (stackA.isEmpty() || stackB.isEmpty())
            return false;
        
        if (stackA.getItem() != stackB.getItem())
            return false;
        
        return stackA.areShareTagsEqual(stackB) && stackA.areCapsCompatible(stackB);
    }
    
    public static boolean consumeItemStack(Container inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (isItemStackEqual(inventory.getItem(i), stack)) {
                int amount = Math.min(stack.getCount(), inventory.getItem(i).getCount());
                if (amount > 0) {
                    inventory.getItem(i).shrink(amount);
                    stack.shrink(amount);
                }
                if (stack.isEmpty())
                    return true;
            }
        }
        return false;
    }
    
    public static boolean addItemStackToInventory(Container inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (isItemStackEqual(inventory.getItem(i), stack)) {
                int amount = Math.min(stack.getMaxStackSize() - inventory.getItem(i).getCount(), stack.getCount());
                if (amount > 0) {
                    ItemStack newStack = stack.copy();
                    newStack.setCount(inventory.getItem(i).getCount() + amount);
                    inventory.setItem(i, newStack);
                    
                    stack.shrink(amount);
                    if (stack.isEmpty())
                        return true;
                }
            }
            
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack);
                return true;
            }
        }
        return false;
    }
    
    public static int getAmount(Container inventory, ItemStack stack) {
        int amount = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (isItemStackEqual(inventory.getItem(i), stack)) {
                amount += inventory.getItem(i).getCount();
            }
        }
        return amount;
    }
    
    public static void cleanInventory(Container inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty())
                inventory.setItem(i, ItemStack.EMPTY);
        }
    }
    
    public static int consume(CreativeIngredient info, Container inventory) {
        return consume(info, inventory, null);
    }
    
    public static int consume(CreativeIngredient info, Container inventory, ArrayList<ItemStack> consumed) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        int stackSize = 1;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && info.is(stack)) {
                
                int used = Math.min(stackSize, stack.getCount());
                stack.shrink(used);
                stackSize -= used;
                ItemStack stackCopy = stack.copy();
                stackCopy.setCount(used);
                stacks.add(stackCopy);
                if (stackSize <= 0)
                    break;
            }
        }
        if (consumed != null)
            consumed.addAll(stacks);
        return stackSize;
    }
    
    public static void sortInventory(Container inventory, boolean alphabetical) {
        List<ItemStack> sorting = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty())
                sorting.add(stack);
        }
        
        if (alphabetical)
            sorting.sort(new Comparator<ItemStack>() {
                
                @Override
                public int compare(ItemStack arg0, ItemStack arg1) {
                    return ForgeRegistries.ITEMS.getKey(arg0.getItem()).toString().compareToIgnoreCase(ForgeRegistries.ITEMS.getKey(arg1.getItem()).toString());
                }
                
            });
        else
            sorting.sort(new Comparator<ItemStack>() {
                
                @Override
                public int compare(ItemStack arg0, ItemStack arg1) {
                    int id0 = Item.getId(arg0.getItem());
                    int id1 = Item.getId(arg1.getItem());
                    if (id0 < id1)
                        return -1;
                    if (id0 > id1)
                        return 1;
                    
                    if (arg0.getDamageValue() < arg1.getDamageValue())
                        return -1;
                    if (arg0.getDamageValue() > arg1.getDamageValue())
                        return 1;
                    
                    return ForgeRegistries.ITEMS.getKey(arg0.getItem()).toString().compareToIgnoreCase(ForgeRegistries.ITEMS.getKey(arg1.getItem()).toString());
                }
                
            });
        
        int maxStackSize = inventory.getMaxStackSize();
        for (int i = 0; i < sorting.size() - 1; i++) {
            ItemStack stack0 = sorting.get(i);
            ItemStack stack1 = sorting.get(i + 1);
            
            if (isItemStackEqual(stack0, stack1)) {
                int maxStack = Math.min(maxStackSize, stack0.getMaxStackSize());
                int use = Math.min(maxStack - stack0.getCount(), stack1.getCount());
                if (use > 0) {
                    stack0.grow(use);
                    stack1.shrink(use);
                    if (stack1.isEmpty())
                        sorting.remove(i + 1);
                    i--;
                }
            }
        }
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, i < sorting.size() ? sorting.get(i) : ItemStack.EMPTY);
        }
        
    }
    
    public static String toString(Container inventory) {
        StringBuilder result = new StringBuilder("[");
        boolean first = true;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                if (first)
                    first = false;
                else
                    result.append(",");
                result.append(stack);
            }
        }
        return result + "]";
    }
    
}
