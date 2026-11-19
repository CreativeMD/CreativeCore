package team.creative.creativecore.client.render.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Source faces are transformed before RenderBox clips them; backends are optional. */
public final class ModelQuadSource {
    private static final List<Provider> PROVIDERS = new CopyOnWriteArrayList<>();
    private ModelQuadSource() {}

    public interface Provider {
        /** null means unhandled; an empty list is an intentional model result. */
        List<BakedQuad> collect(BakedModel model, LevelAccessor level, BlockPos pos, BlockState state,
            Direction side, RandomSource random, ModelData data, RenderType layer);
    }

    public static void register(Provider provider) { PROVIDERS.add(provider); }

    public static ModelData prepare(ModelData data, long seed) {
        return data.derive().with(UnculledQuadCache.PROPERTY, new UnculledQuadCache(seed)).build();
    }

    public static List<BakedQuad> collect(BakedModel model, LevelAccessor level, BlockPos pos, BlockState state,
            Direction side, RandomSource random, ModelData data, RenderType layer) {
        List<BakedQuad> directional = raw(model, level, pos, state, side, random, data, layer);
        long seed = state != null && pos != null ? state.getSeed(pos) : 0;
        UnculledQuadCache cache = data.get(UnculledQuadCache.PROPERTY);
        if (cache == null) cache = new UnculledQuadCache(seed);
        return cache.append(side, layer, directional,
            () -> raw(model, level, pos, state, null, RandomSource.create(seed), data, layer));
    }

    private static List<BakedQuad> raw(BakedModel model, LevelAccessor level, BlockPos pos, BlockState state,
            Direction side, RandomSource random, ModelData data, RenderType layer) {
        for (Provider provider : PROVIDERS) {
            List<BakedQuad> quads = provider.collect(model, level, pos, state, side, random, data, layer);
            if (quads != null) return quads;
        }
        return model.getQuads(state, side, random, data, layer);
    }
}
