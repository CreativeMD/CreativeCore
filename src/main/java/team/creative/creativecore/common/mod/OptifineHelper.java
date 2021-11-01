package team.creative.creativecore.common.mod;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
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
    
    public static List<BakedQuad> getBakedQuad(List<BakedQuad> quads, LevelAccessor level, BlockState state, Facing facing, BlockPos pos, RenderType layer, Random rand) {
        return quads;
    }
    
}
