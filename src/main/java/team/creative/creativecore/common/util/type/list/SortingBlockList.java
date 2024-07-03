package team.creative.creativecore.common.util.type.list;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.premade.RegistryObjectListConfig;

public class SortingBlockList {
    
    //private List<TagKey<Block>> tags;
    @CreativeConfig
    public RegistryObjectListConfig<Block> blocks = new RegistryObjectListConfig<>(BuiltInRegistries.BLOCK);
    
    /*public SortingBlockList add(TagKey<Block> tag) {
        tags.add(tag);
        return this;
    }*/
    
    public SortingBlockList add(Block block) {
        blocks.add(block.builtInRegistryHolder().unwrapKey().get().location());
        return this;
    }
    
    public boolean is(BlockState state) {
        return is(state.getBlock());
    }
    
    public boolean is(Block block) {
        /*for (TagKey<Block> tag : tags)
            if (block.builtInRegistryHolder().is(tag))
                return true;*/
        for (Block b : blocks)
            if (b == block)
                return true;
        return false;
    }
    
}
