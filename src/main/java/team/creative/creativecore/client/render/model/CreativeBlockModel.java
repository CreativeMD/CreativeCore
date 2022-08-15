package team.creative.creativecore.client.render.model;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import team.creative.creativecore.client.render.box.RenderBox;

@OnlyIn(Dist.CLIENT)
public abstract class CreativeBlockModel {
    
    public abstract List<? extends RenderBox> getBoxes(BlockState state, IModelData data, Random source);
    
    public abstract @NotNull IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData);
    
}