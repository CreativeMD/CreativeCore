package com.creativemd.creativecore.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityCreative extends TileEntity {
    
    private World tempWorld;
    
    public boolean isClientSide() {
        if (world != null)
            return world.isRemote;
        return tempWorld.isRemote;
    }
    
    public World getTempWorld() {
        return tempWorld;
    }
    
    @Override
    protected void setWorldCreate(World worldIn) {
        tempWorld = worldIn;
    }
    
    public void deleteTempWorld() {
        tempWorld = null;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdate(pkt.getNbtCompound(), false);
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        handleUpdate(tag, true);
    }
    
    public abstract void handleUpdate(NBTTagCompound nbt, boolean chunkUpdate);
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    
    public double getDistance(BlockPos coord) {
        return Math.sqrt(pos.distanceSq(coord));
    }
    
    @SideOnly(Side.CLIENT)
    public void updateRender() {
        world.markBlockRangeForRenderUpdate(pos, pos);
    }
    
    public void updateBlock() {
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.markChunkDirty(getPos(), this);
        }
    }
    
}
