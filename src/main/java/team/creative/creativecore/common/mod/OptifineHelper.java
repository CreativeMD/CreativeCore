package team.creative.creativecore.common.mod;

import java.util.List;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import team.creative.creativecore.common.util.math.base.Facing;

public class OptifineHelper {
    
    public static boolean installed() {
        return false;
    }
    
    public static boolean isEmissive(TextureAtlasSprite sprite) {
        return false;
    }
    
    public static BakedModel getRenderModel(BakedModel model, LevelAccessor level, BlockState state, BlockPos pos) {
        return model;
    }
    
    public static List<BakedQuad> getBakedQuad(List<BakedQuad> quads, LevelAccessor level, BlockState state, Facing facing, BlockPos pos, RenderType layer, RandomSource rand) {
        return quads;
    }
    
    public static boolean isShaders() {
        return false;
    }
    
    public static void preRenderChunkLayer(RenderType type) {}
    
    public static void postRenderChunkLayer(RenderType type) {}
    
    public static boolean isRenderRegions() {
        return false;
    }
    
    public static boolean isAnisotropicFiltering() {
        return false;
    }
    
    public static boolean isAntialiasing() {
        return false;
    }
    
    public static void pushBuffer(BlockState state, BlockPos pos, LevelAccessor level, BufferBuilder builder) {}
    
    public static void popBuffer(BufferBuilder builder) {}
    
    public static void calcNormalChunkLayer(BufferBuilder builder) {}
    
}
