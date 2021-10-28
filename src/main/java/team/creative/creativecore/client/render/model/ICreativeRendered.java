package team.creative.creativecore.client.render.model;

import java.util.List;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.math.base.Facing;

public interface ICreativeRendered {
    
    @OnlyIn(value = Dist.CLIENT)
    public List<? extends RenderBox> getRenderingBoxes(BlockState state, BlockEntity be, ItemStack stack);
    
    @OnlyIn(value = Dist.CLIENT)
    public default void applyCustomOpenGLHackery(ItemStack stack, TransformType cameraTransformType) {}
    
    @OnlyIn(value = Dist.CLIENT)
    public default List<BakedQuad> getCachedModel(Facing facing, RenderType layer, BlockState state, BlockEntity be, ItemStack stack, boolean threaded) {
        return null;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void saveCachedModel(Facing facing, RenderType layer, List<BakedQuad> cachedQuads, BlockState state, BlockEntity be, ItemStack stack, boolean threaded) {}
}