package team.creative.creativecore.client.render.box.bridge.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
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
 * Indigo-flavoured {@link RenderContext} that captures emitted quads back into a
 * {@link BakedQuad} list. Reused per-thread by {@link FabricBridgeImpl}.
 */
final class IndigoCollectingRenderContext implements RenderContext {
    
    private final List<BakedQuad> collectedQuads = new ArrayList<>();
    private final List<QuadTransform> transformStack = new ArrayList<>();
    private final CollectingQuadEmitter editorQuad = new CollectingQuadEmitter();
    
    private ModelData modelData = ModelData.EMPTY;
    private RenderType renderType;
    private TriState fabricUsesAmbientOcclusion = TriState.DEFAULT;
    
    private BlockAndTintGetter blockView;
    private BlockPos pos;
    private Supplier<RandomSource> randomSupplier;
    
    void prepare(ModelData modelData, RenderType renderType) {
        this.modelData = modelData;
        this.renderType = renderType;
        this.collectedQuads.clear();
        this.transformStack.clear();
        this.fabricUsesAmbientOcclusion = TriState.DEFAULT;
    }
    
    void setEmitContext(BlockAndTintGetter blockView, BlockPos pos, Supplier<RandomSource> randomSupplier) {
        this.blockView = blockView;
        this.pos = pos;
        this.randomSupplier = randomSupplier;
    }
    
    List<BakedQuad> collectQuads() {
        return new ArrayList<>(collectedQuads);
    }
    
    void reset() {
        collectedQuads.clear();
        transformStack.clear();
        modelData = ModelData.EMPTY;
        renderType = null;
        fabricUsesAmbientOcclusion = TriState.DEFAULT;
        blockView = null;
        pos = null;
        randomSupplier = null;
    }
    
    @Override
    public QuadEmitter getEmitter() {
        editorQuad.clear();
        return editorQuad;
    }
    
    @Override
    public boolean hasTransform() {
        return !transformStack.isEmpty();
    }
    
    @Override
    public void pushTransform(QuadTransform transform) {
        transformStack.add(transform);
    }
    
    @Override
    public void popTransform() {
        if (!transformStack.isEmpty()) {
            transformStack.remove(transformStack.size() - 1);
        }
    }
    
    @Override
    public boolean isFaceCulled(@Nullable Direction face) {
        return false;
    }
    
    @Override
    public BakedModelConsumer bakedModelConsumer() {
        return new BakedModelConsumer() {
            @Override
            public void accept(BakedModel model) {
                accept(model, null);
            }
            
            @Override
            public void accept(BakedModel model, @Nullable BlockState state) {
                if (model instanceof FabricBakedModel fabricModel && !fabricModel.isVanillaAdapter()) {
                    if (blockView != null && pos != null && randomSupplier != null && state != null) {
                        fabricModel.emitBlockQuads(blockView, state, pos, randomSupplier, IndigoCollectingRenderContext.this);
                        return;
                    }
                }
                VanillaModelEncoderHelper.emitBlockQuads(model, state, () -> RandomSource.create(), IndigoCollectingRenderContext.this);
            }
        };
    }
    
    @Override
    public ModelData getModelData() {
        return modelData;
    }
    
    @Override
    public RenderType getRenderType() {
        return renderType;
    }
    
    @Override
    public void pushModelData(ModelData modelData) {
        this.modelData = modelData;
    }
    
    @Override
    public void popModelData() {
    }
    
    @Override
    public TriState usesAmbientOcclusion() {
        return fabricUsesAmbientOcclusion;
    }
    
    @Override
    public void setUsesAmbientOcclusion(TriState state) {
        this.fabricUsesAmbientOcclusion = state;
    }
    
    private class CollectingQuadEmitter extends MutableQuadViewImpl {
        {
            data = new int[EncodingFormat.TOTAL_STRIDE];
            clear();
        }
        
        @Override
        public void emitDirectly() {
            computeGeometry();
            
            if (!applyTransforms(this)) {
                return;
            }
            
            int[] vanillaData = new int[32];
            System.arraycopy(data, EncodingFormat.HEADER_STRIDE, vanillaData, 0, 32);
            
            for (int i = 0; i < 4; i++) {
                int colorIndex = i * 8 + 3;
                vanillaData[colorIndex] = ColorHelper.toVanillaColor(vanillaData[colorIndex]);
            }
            
            Direction face = lightFace();
            int tintIndex = colorIndex();
            
            BakedQuad bakedQuad = new BakedQuad(vanillaData, tintIndex, face != null ? face : Direction.DOWN, null, true);
            collectedQuads.add(bakedQuad);
        }
        
        private boolean applyTransforms(MutableQuadViewImpl quad) {
            for (int i = 0; i < transformStack.size(); i++) {
                QuadTransform t = transformStack.get(i);
                if (!t.transform(quad)) {
                    return false;
                }
            }
            return true;
        }
    }
}
