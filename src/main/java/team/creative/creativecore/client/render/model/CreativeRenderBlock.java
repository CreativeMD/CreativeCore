package team.creative.creativecore.client.render.model;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;

@OnlyIn(value = Dist.CLIENT)
public abstract class CreativeRenderBlock {
    
    public abstract List<? extends RenderBox> getBoxes(BlockState state);
    
}