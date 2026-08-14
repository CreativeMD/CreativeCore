package team.creative.creativecore.common.util.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import team.creative.creativecore.common.util.CompoundSerializer;
import team.creative.creativecore.common.util.registry.exception.RegistryException;
import team.creative.creativecore.common.util.type.list.SingletonList;

public abstract class BlockFilter implements Filter<Block>, CompoundSerializer {
    
    public static final FilterRegistry<BlockFilter> REGISTRY = new FilterRegistry<BlockFilter>();
    public static final BlockFilter ANY = new BlockFilter() {
        
        @Override
        public boolean is(Block t) {
            return true;
        }
        
        @Override
        protected void writeInternal(CompoundTag nbt) {}
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            return provider.lookupOrThrow(Registries.BLOCK).listElements().map(Reference::value).toList();
        }
    };
    public static final BlockFilterProperty AXIS = new BlockFilterProperty(BlockStateProperties.AXIS);
    public static final BlockFilterProperty FACING = new BlockFilterProperty(BlockStateProperties.FACING);
    
    public static BlockFilter block(Block block) {
        return new BlockFilterBlock(block);
    }
    
    public static BlockFilter blocks(Block... blocks) {
        return new BlockFilterBlocks(blocks);
    }
    
    public static BlockFilter instance(Class<? extends Block> clazz) {
        return new BlockFilterClass(clazz);
    }
    
    public static BlockFilter tag(TagKey<Block> tag) {
        return new BlockFilterTag(tag);
    }
    
    static {
        REGISTRY.register("b", BlockFilterBlock.class, BlockFilterBlock::new);
        REGISTRY.register("s", BlockFilterBlocks.class, BlockFilterBlocks::new);
        REGISTRY.register("c", BlockFilterClass.class, BlockFilterClass::new);
        REGISTRY.register("ta", BlockFilterTag.class, BlockFilterTag::new);
        REGISTRY.registerStatic("any", BlockFilter.class, ANY);
        REGISTRY.registerStatic("a", BlockFilterProperty.class, AXIS);
        REGISTRY.registerStatic("f", BlockFilterProperty.class, FACING);
    }
    
    public abstract Collection<Block> getPossibleBlocks(HolderLookup.Provider provider);
    
    @Override
    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        writeInternal(nbt);
        try {
            nbt.putString("t", REGISTRY.getId(this));
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
        return nbt;
    }
    
    protected abstract void writeInternal(CompoundTag nbt);
    
    private static class BlockFilterBlock extends BlockFilter {
        
        public final Block block;
        
        public BlockFilterBlock(Block block) {
            this.block = block;
        }
        
        @SuppressWarnings("unused")
        public BlockFilterBlock(CompoundTag nbt) {
            this.block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("b")));
        }
        
        @Override
        public boolean is(Block t) {
            return t == block;
        }
        
        @Override
        protected void writeInternal(CompoundTag nbt) {
            nbt.putString("b", BuiltInRegistries.BLOCK.getKey(block).toString());
        }
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            return new SingletonList<Block>(block);
        }
        
    }
    
    private static class BlockFilterBlocks extends BlockFilter {
        
        public final Block[] blocks;
        
        public BlockFilterBlocks(Block... blocks) {
            this.blocks = blocks;
        }
        
        @SuppressWarnings("unused")
        public BlockFilterBlocks(CompoundTag nbt) {
            ListTag list = nbt.getList("b", Tag.TAG_STRING);
            this.blocks = new Block[list.size()];
            for (int i = 0; i < blocks.length; i++)
                blocks[i] = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(list.getString(i)));
        }
        
        @Override
        public boolean is(Block t) {
            return ArrayUtils.contains(blocks, t);
        }
        
        @Override
        protected void writeInternal(CompoundTag nbt) {
            ListTag list = new ListTag();
            for (int i = 0; i < blocks.length; i++)
                list.add(StringTag.valueOf(BuiltInRegistries.BLOCK.getKey(blocks[i]).toString()));
            nbt.put("b", list);
        }
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            return Arrays.asList(blocks);
        }
        
    }
    
    private static class BlockFilterClass extends BlockFilter {
        
        public final Class<? extends Block> clazz;
        
        public BlockFilterClass(Class<? extends Block> clazz) {
            this.clazz = clazz;
        }
        
        @SuppressWarnings("unused")
        public BlockFilterClass(CompoundTag nbt) {
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
        protected void writeInternal(CompoundTag nbt) {
            nbt.putString("c", clazz.getName());
        }
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            return provider.lookupOrThrow(Registries.BLOCK).listElements().filter(x -> is(x.value())).map(Reference::value).toList();
        }
    }
    
    public static class BlockFilterProperty extends BlockFilter {
        
        public final Property<?> property;
        
        public BlockFilterProperty(Property<?> property) {
            this.property = property;
        }
        
        @Override
        public boolean is(Block t) {
            return t.defaultBlockState().hasProperty(property);
        }
        
        @Override
        protected void writeInternal(CompoundTag nbt) {}
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            return provider.lookupOrThrow(Registries.BLOCK).listElements().filter(x -> is(x.value())).map(Reference::value).toList();
        }
    }
    
    private static class BlockFilterTag extends BlockFilter {
        
        public final TagKey<Block> tag;
        
        public BlockFilterTag(TagKey<Block> tag) {
            this.tag = tag;
        }
        
        @SuppressWarnings("unused")
        public BlockFilterTag(CompoundTag nbt) {
            tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(nbt.getString("t")));
        }
        
        @Override
        protected void writeInternal(CompoundTag nbt) {
            nbt.putString("t", tag.location().toString());
        }
        
        @Override
        public boolean is(Block t) {
            return t.builtInRegistryHolder().is(tag);
        }
        
        @Override
        public Collection<Block> getPossibleBlocks(Provider provider) {
            var o = provider.lookupOrThrow(tag.registry()).get(tag);
            if (o.isPresent()) {
                List<Block> list = new ArrayList<>();
                for (Holder<Block> holder : o.get())
                    list.add(holder.value());
                return list;
            }
            return Collections.EMPTY_LIST;
        }
        
    }
    
}
