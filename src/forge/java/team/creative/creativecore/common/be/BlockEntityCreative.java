package team.creative.creativecore.common.be;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public abstract class BlockEntityCreative extends BlockEntity {
    
    public BlockEntityCreative(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public boolean isClient() {
        if (level != null)
            return level.isClientSide;
        return EffectiveSide.get().isClient();
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdate(pkt.getTag(), false);
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        handleUpdate(tag, true);
    }
    
    public abstract void handleUpdate(CompoundTag nbt, boolean chunkUpdate);
    
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }
    
    public void markDirty() {
        level.blockEntityChanged(worldPosition);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    
}
