package team.creative.creativecore.client.render.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRenderChunkSupplier {
    
    public Object getRenderChunk(World world, BlockPos pos);
    
}
