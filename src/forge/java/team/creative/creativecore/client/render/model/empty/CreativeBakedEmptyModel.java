package team.creative.creativecore.client.render.model.empty;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CreativeBakedEmptyModel implements IDynamicBakedModel {
    
    public final TextureAtlasSprite particle;
    
    public CreativeBakedEmptyModel(TextureAtlasSprite particle) {
        this.particle = particle;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }
    
    @Override
    public boolean isGui3d() {
        return false;
    }
    
    @Override
    public boolean usesBlockLight() {
        return false;
    }
    
    @Override
    public boolean isCustomRenderer() {
        return true;
    }
    
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }
    
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
    
    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
    
}
