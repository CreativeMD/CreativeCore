package team.creative.creativecore.common.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockGetterFake implements BlockGetter {
    
    public BlockGetter parent;
    public BlockPos pos;
    public BlockState fakeState;
    
    public BlockGetterFake() {}
    
    public void set(BlockGetter world, BlockPos pos, BlockState fakeState) {
        this.parent = world;
        this.pos = pos;
        this.fakeState = fakeState;
    }
    
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            return null;
        return parent.getBlockEntity(pos);
    }
    
    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos))
            return fakeState;
        return parent.getBlockState(pos);
    }
    
    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (pos.equals(this.pos))
            return null;
        return parent.getFluidState(pos);
    }
    
    @Override
    public int getHeight() {
        return parent.getHeight();
    }
    
    @Override
    public int getMinBuildHeight() {
        return parent.getMinBuildHeight();
    }
    
}
