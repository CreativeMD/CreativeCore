package com.creativemd.creativecore.common.utils.sorting;

import com.creativemd.creativecore.common.utils.mc.BlockUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public abstract class BlockSelector {
    
    public abstract boolean is(Block block, int meta);
    
    public static class BlockSelectorAnd extends BlockSelector {
        
        public final BlockSelector[] selectors;
        
        public BlockSelectorAnd(BlockSelector... selectors) {
            this.selectors = selectors;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            for (BlockSelector selector : selectors)
                if (!selector.is(block, meta))
                    return false;
            return true;
        }
        
    }
    
    public static class BlockSelectorOr extends BlockSelector {
        
        public final BlockSelector[] selectors;
        
        public BlockSelectorOr(BlockSelector... selectors) {
            this.selectors = selectors;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            for (BlockSelector selector : selectors)
                if (selector.is(block, meta))
                    return true;
            return false;
        }
    }
    
    public static class BlockSelectorBlock extends BlockSelector {
        
        public final Block block;
        
        public BlockSelectorBlock(Block block) {
            this.block = block;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            return this.block == block;
        }
        
    }
    
    public static class BlockSelectorBlocks extends BlockSelector {
        
        public Block[] blocks;
        
        public BlockSelectorBlocks(Block... blocks) {
            this.blocks = blocks;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            for (Block searchBlock : blocks)
                if (searchBlock == block)
                    return true;
            return false;
        }
        
    }
    
    public static class BlockSelectorState extends BlockSelector {
        
        public final Block block;
        public final int meta;
        
        public BlockSelectorState(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            return this.block == block && this.meta == meta;
        }
    }
    
    public static class BlockSelectorMaterial extends BlockSelector {
        
        public final Material material;
        
        public BlockSelectorMaterial(Material material) {
            this.material = material;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            return block.getStateFromMeta(meta).getMaterial() == material;
        }
    }
    
    public static class BlockSelectorClass extends BlockSelector {
        
        public final Class<? extends Block>[] classes;
        
        public BlockSelectorClass(Class<? extends Block>... classes) {
            this.classes = classes;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            for (Class<? extends Block> clazz : classes)
                if (clazz.isInstance(block))
                    return true;
            return false;
        }
        
    }
    
    public static class BlockSelectorProperty extends BlockSelector {
        
        public final IProperty[] properties;
        
        public BlockSelectorProperty(IProperty... properties) {
            this.properties = properties;
        }
        
        @Override
        public boolean is(Block block, int meta) {
            IBlockState state = BlockUtils.getState(block, meta);
            for (IProperty iProperty : properties)
                if (state.getPropertyKeys().contains(iProperty))
                    return true;
            return false;
        }
        
    }
    
}
