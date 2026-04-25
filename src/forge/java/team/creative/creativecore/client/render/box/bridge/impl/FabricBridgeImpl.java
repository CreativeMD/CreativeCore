package team.creative.creativecore.client.render.box.bridge.impl;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * The actual implementation of the Fabric Renderer bridge. Lazily loaded by
 * {@link team.creative.creativecore.client.render.box.bridge.FabricRendererBridge}
 * once the Fabric Renderer API has been confirmed present on the classpath.
 */
public final class FabricBridgeImpl {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("CreativeCore/FabricRendererBridge");
    
    private static final ThreadLocal<IndigoCollectingRenderContext> INDIGO_CONTEXT = ThreadLocal.withInitial(IndigoCollectingRenderContext::new);
    private static final ThreadLocal<SodiumCollectingRenderContext> SODIUM_CONTEXT = ThreadLocal.withInitial(SodiumCollectingRenderContext::new);
    
    private FabricBridgeImpl() {}
    
    @Nullable
    public static List<BakedQuad> tryBridge(BakedModel model, BlockState state, @Nullable Direction direction,
            RandomSource random, ModelData modelData, RenderType layer,
            BlockAndTintGetter blockView, BlockPos pos, boolean sodiumPresent) {
        // We deliberately limit bridging to ForwardingBakedModel + FabricBakedModel because that's
        // the canonical shape of CTM/connected-model wrappers (Continuity, Indium, etc.). Models
        // that implement FabricBakedModel directly without forwarding are typically self-contained
        // and benefit nothing from a getQuads -> emitBlockQuads -> getQuads round-trip; routing
        // them through here would also pay the per-direction emit cost (see below) for no gain.
        if (!(model instanceof ForwardingBakedModel) || !(model instanceof FabricBakedModel fabricModel)) return null;
        if (fabricModel.isVanillaAdapter()) return null;
        
        try {
            final long seed = state.getSeed(pos);
            Supplier<RandomSource> randomSupplier = () -> {
                random.setSeed(seed);
                return random;
            };
            
            // Note: emitBlockQuads emits the model's full quad set; RenderBox.getBakedQuad calls
            // us once per face direction, so a CTM-wrapped block effectively pays 6x emit cost
            // here. In practice Continuity caches its processed quads per (state, pos, view,
            // type) so subsequent face calls hit that cache. If this ever becomes a hot spot,
            // a per-block-render quad cache keyed by (model, pos, type) would amortise it.
            List<BakedQuad> quads;
            if (sodiumPresent) {
                quads = bridgeWithSodium(fabricModel, state, blockView, pos, randomSupplier, modelData, layer);
            } else {
                quads = bridgeWithIndigo(fabricModel, state, blockView, pos, randomSupplier, modelData, layer);
            }
            
            if (direction != null) {
                quads.removeIf(quad -> {
                    Direction quadDir = quad.getDirection();
                    return quadDir != null && quadDir != direction;
                });
            }
            return quads;
        } catch (Exception e) {
            LOGGER.warn("Failed to bridge emitBlockQuads for state {}, falling back to getQuads", state, e);
            return null;
        }
    }
    
    private static List<BakedQuad> bridgeWithSodium(FabricBakedModel model, BlockState state, BlockAndTintGetter blockView,
            BlockPos pos, Supplier<RandomSource> randomSupplier, ModelData modelData, RenderType layer) {
        SodiumCollectingRenderContext context = SODIUM_CONTEXT.get();
        try {
            context.prepare(blockView, state, pos, modelData, layer, randomSupplier);
            model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            return context.collectQuads();
        } finally {
            // Reset unconditionally so an exception inside emitBlockQuads cannot leak stale
            // collected quads / state into the next call on this thread.
            context.reset();
        }
    }
    
    private static List<BakedQuad> bridgeWithIndigo(FabricBakedModel model, BlockState state, BlockAndTintGetter blockView,
            BlockPos pos, Supplier<RandomSource> randomSupplier, ModelData modelData, RenderType layer) {
        IndigoCollectingRenderContext context = INDIGO_CONTEXT.get();
        try {
            context.prepare(modelData, layer);
            context.setEmitContext(blockView, pos, randomSupplier);
            model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            return context.collectQuads();
        } finally {
            // Reset unconditionally so an exception inside emitBlockQuads cannot leak stale
            // collected quads / transform stack into the next call on this thread.
            context.reset();
        }
    }
}
