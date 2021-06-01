package team.creative.creativecore.client.render.model;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;

public interface ICreativeRendered {
    
    @OnlyIn(value = Dist.CLIENT)
    public List<? extends RenderBox> getRenderingCubes(BlockState state, TileEntity te, ItemStack stack);
    
    @OnlyIn(value = Dist.CLIENT)
    public default void applyCustomOpenGLHackery(ItemStack stack, TransformType cameraTransformType) {}
    
    @OnlyIn(value = Dist.CLIENT)
    public default List<BakedQuad> getCachedModel(Direction facing, BlockState state, TileEntity te, ItemStack stack, boolean threaded) {
        return null;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public default void saveCachedModel(Direction facing, List<BakedQuad> cachedQuads, BlockState state, TileEntity te, ItemStack stack, boolean threaded) {}
}