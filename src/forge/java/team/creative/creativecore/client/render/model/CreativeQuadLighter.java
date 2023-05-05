package team.creative.creativecore.client.render.model;

import net.minecraft.world.level.block.state.BlockState;

public interface CreativeQuadLighter {
    
    public void setState(BlockState state);
    
    public void setCustomTint(int tint);
    
}
