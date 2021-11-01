package team.creative.creativecore.common.util.filter.premade;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.filter.Filter;

public class ItemFilters {
    
    public static Filter<Item> item(Item item) {
        return new ItemFilter(item);
    }
    
    public static Filter<Item> items(Item... items) {
        return new ItemsFilter(items);
    }
    
    public static Filter<Item> instance(Class<? extends Item> clazz) {
        return new ItemClassFilter(clazz);
    }
    
    public static Filter<Item> and(Filter<Item>... filters) {
        return Filter.and(filters);
    }
    
    public static Filter<Item> or(Filter<Item>... filters) {
        return Filter.or(filters);
    }
    
    public static Filter<Item> not(Filter<Item> filter) {
        return Filter.not(filter);
    }
    
    static {
        Filter.SERIALIZER.register("item", ItemFilter.class);
        Filter.SERIALIZER.register("items", ItemsFilter.class);
        Filter.SERIALIZER.register("iclass", ItemClassFilter.class);
    }
    
    private static class ItemFilter implements Filter<Item>, CompoundSerializer {
        
        public final Item item;
        
        public ItemFilter(Item item) {
            this.item = item;
        }
        
        @SuppressWarnings("unused")
        public ItemFilter(CompoundTag nbt) {
            this.item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("i")));
        }
        
        @Override
        public boolean is(Item t) {
            return t == item;
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("i", item.getRegistryName().toString());
            return nbt;
        }
        
    }
    
    private static class ItemsFilter implements Filter<Item>, CompoundSerializer {
        
        public final Item[] items;
        
        public ItemsFilter(Item... items) {
            this.items = items;
        }
        
        @SuppressWarnings("unused")
        public ItemsFilter(CompoundTag nbt) {
            ListTag list = nbt.getList("i", Tag.TAG_STRING);
            this.items = new Item[list.size()];
            for (int i = 0; i < items.length; i++)
                items[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(list.getString(i)));
        }
        
        @Override
        public boolean is(Item t) {
            return ArrayUtils.contains(items, t);
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            ListTag list = new ListTag();
            for (int i = 0; i < items.length; i++)
                list.add(StringTag.valueOf(items[i].getRegistryName().toString()));
            nbt.put("i", list);
            return nbt;
        }
        
    }
    
    private static class ItemClassFilter implements Filter<Item>, CompoundSerializer {
        
        public final Class<? extends Item> clazz;
        
        public ItemClassFilter(Class<? extends Item> clazz) {
            this.clazz = clazz;
        }
        
        @SuppressWarnings("unused")
        public ItemClassFilter(CompoundTag nbt) {
            Class temp = null;
            try {
                temp = Class.forName(nbt.getString("c"));
            } catch (Exception e) {}
            clazz = temp;
        }
        
        @Override
        public boolean is(Item t) {
            return clazz != null && clazz.isInstance(t);
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("c", clazz.getName());
            return nbt;
        }
        
    }
}
