package team.creative.creativecore.common.util.filter.premade;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.ArrayUtils;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.filter.Filter;

public class BlockFilters {
    
    public static Filter<Block> block(Block block) {
        return new BlockFilter(block);
    }
    
    public static Filter<Block> blocks(Block... blocks) {
        return new BlocksFilter(blocks);
    }
    
    public static Filter<Block> instance(Class<? extends Block> clazz) {
        return new BlockClassFilter(clazz);
    }
    
    public static Filter<Block> and(Filter<Block>... filters) {
        return Filter.and(filters);
    }
    
    public static Filter<Block> or(Filter<Block>... filters) {
        return Filter.or(filters);
    }
    
    public static Filter<Block> not(Filter<Block> filter) {
        return Filter.not(filter);
    }
    
    public static Filter<Block> property(Property<?> property) {
        return new BlockPropertyFilter(property);
    }
    
    public static Filter<Block> tag(TagKey<Block> tag) {
        return new BlockTagFilter(tag);
    }
    
    static {
        Filter.SERIALIZER.register("block", BlockFilter.class);
        Filter.SERIALIZER.register("blocks", BlocksFilter.class);
        Filter.SERIALIZER.register("bclass", BlockClassFilter.class);
    }
    
    private static class BlockFilter implements Filter<Block>, CompoundSerializer {
        
        public final Block block;
        
        public BlockFilter(Block block) {
            this.block = block;
        }
        
        @SuppressWarnings("unused")
        public BlockFilter(CompoundTag nbt) {
            this.block = Registry.BLOCK.get(new ResourceLocation(nbt.getString("b")));
        }
        
        @Override
        public boolean is(Block t) {
            return t == block;
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("b", Registry.BLOCK.getKey(block).toString());
            return nbt;
        }
        
    }
    
    private static class BlocksFilter implements Filter<Block>, CompoundSerializer {
        
        public final Block[] blocks;
        
        public BlocksFilter(Block... blocks) {
            this.blocks = blocks;
        }
        
        @SuppressWarnings("unused")
        public BlocksFilter(CompoundTag nbt) {
            ListTag list = nbt.getList("b", Tag.TAG_STRING);
            this.blocks = new Block[list.size()];
            for (int i = 0; i < blocks.length; i++)
                blocks[i] = Registry.BLOCK.get(new ResourceLocation(list.getString(i)));
        }
        
        @Override
        public boolean is(Block t) {
            return ArrayUtils.contains(blocks, t);
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            ListTag list = new ListTag();
            for (int i = 0; i < blocks.length; i++)
                list.add(StringTag.valueOf(Registry.BLOCK.getKey(blocks[i]).toString()));
            nbt.put("b", list);
            return nbt;
        }
        
    }
    
    private static class BlockClassFilter implements Filter<Block>, CompoundSerializer {
        
        public final Class<? extends Block> clazz;
        
        public BlockClassFilter(Class<? extends Block> clazz) {
            this.clazz = clazz;
        }
        
        @SuppressWarnings("unused")
        public BlockClassFilter(CompoundTag nbt) {
            Class temp = null;
            try {
                temp = Class.forName(nbt.getString("c"));
            } catch (Exception e) {}
            clazz = temp;
        }
        
        @Override
        public boolean is(Block t) {
            return clazz != null && clazz.isInstance(t);
        }
        
        @Override
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("c", clazz.getName());
            return nbt;
        }
        
    }
    
    private record BlockPropertyFilter(Property<?> property) implements Filter<Block> {
        
        @Override
        public boolean is(Block t) {
            return t.defaultBlockState().hasProperty(property);
        }
    }
    
    private record BlockTagFilter(TagKey<Block> tag) implements Filter<Block> {
        
        @Override
        public boolean is(Block t) {
            return t.builtInRegistryHolder().is(tag);
        }
        
    }
}
