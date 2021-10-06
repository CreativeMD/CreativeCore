package team.creative.creativecore.common.util.filter.block;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.world.level.block.Block;
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
    
    private static class BlockFilter implements Filter<Block> {
        
        public final Block block;
        
        public BlockFilter(Block block) {
            this.block = block;
        }
        
        @Override
        public boolean is(Block t) {
            return t == block;
        }
        
    }
    
    private static class BlocksFilter implements Filter<Block> {
        
        public final Block[] blocks;
        
        public BlocksFilter(Block... blocks) {
            this.blocks = blocks;
        }
        
        @Override
        public boolean is(Block t) {
            return ArrayUtils.contains(blocks, t);
        }
        
    }
    
    private static class BlockClassFilter implements Filter<Block> {
        
        public final Class<? extends Block> clazz;
        
        public BlockClassFilter(Class<? extends Block> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        public boolean is(Block t) {
            return clazz.isInstance(t);
        }
        
    }
}
