package team.creative.creativecore.common.world;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class IBlockAccessFake implements IBlockReader {
    
    public IBlockReader parent;
    public BlockPos pos;
    public BlockState fakeState;
    
    public IBlockAccessFake() {
        
    }
    
    public void set(IBlockReader world, BlockPos pos, BlockState fakeState) {
        this.parent = world;
        this.pos = pos;
        this.fakeState = fakeState;
    }
    
    @Override
    public TileEntity getBlockEntity(BlockPos pos) {
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
    
}
