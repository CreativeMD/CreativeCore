package team.creative.creativecore.client.render.model;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Owned by one LittleTiles model-data evaluation, never shared across workers or rebuilds. */
public final class UnculledQuadCache {
    public static final ModelProperty<UnculledQuadCache> PROPERTY = new ModelProperty<>();
    private static final List<List<BakedQuad>> EMPTY_FACES = List.of(List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

    private final long seed;
    private final Map<RenderType, List<List<BakedQuad>>> layers = new IdentityHashMap<>(4);

    public UnculledQuadCache(long seed) {
        this.seed = seed;
    }

    public List<BakedQuad> append(BakedModel model, BlockState state, Direction side, ModelData data,
            RenderType layer, List<BakedQuad> directional) {
        return append(side, layer, directional,
            () -> model.getQuads(state, null, RandomSource.create(seed), data, layer));
    }

    /** Allows the combined backend to process unculled faces through CTM before grouping. */
    public List<BakedQuad> append(Direction side, RenderType layer, List<BakedQuad> directional,
            Supplier<List<BakedQuad>> unculled) {
        if (side == null)
            return directional;
        List<List<BakedQuad>> faces = layers.get(layer);
        if (faces == null) {
            // Separate position-seeded RNG: do not consume the caller's random stream.
            // Custom models consuming RNG in getRenderTypes need in-game validation.
            faces = group(unculled.get());
            layers.put(layer, faces);
        }
        List<BakedQuad> additional = faces.get(side.ordinal());
        if (additional.isEmpty())
            return directional;
        if (directional.isEmpty())
            return additional;
        List<BakedQuad> merged = new ArrayList<>(directional.size() + additional.size());
        merged.addAll(directional);
        merged.addAll(additional);
        return merged;
    }

    private static List<List<BakedQuad>> group(List<BakedQuad> quads) {
        if (quads.isEmpty())
            return EMPTY_FACES;
        List<List<BakedQuad>> faces = new ArrayList<>(EMPTY_FACES);
        for (BakedQuad quad : quads) {
            int index = quad.getDirection().ordinal();
            List<BakedQuad> face = faces.get(index);
            if (face.isEmpty()) {
                face = new ArrayList<>(2);
                faces.set(index, face);
            }
            face.add(quad);
        }
        return faces;
    }
}
