package team.creative.creativecore.client.render.model;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import team.creative.creativecore.client.render.box.RenderBox;

@OnlyIn(Dist.CLIENT)
public abstract class CreativeBlockModel {
    
    public abstract List<? extends RenderBox> getBoxes(BlockState state, ModelData data, RandomSource source);
    
    public abstract @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData);
    
}