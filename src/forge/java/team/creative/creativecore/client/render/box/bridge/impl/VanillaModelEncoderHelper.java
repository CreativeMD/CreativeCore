package team.creative.creativecore.client.render.box.bridge.impl;

import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

final class VanillaModelEncoderHelper {
    
    private VanillaModelEncoderHelper() {}
    
    static void emitBlockQuads(BakedModel model, BlockState state, Supplier<RandomSource> randomSupplier, RenderContext context) {
        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            Direction cullFace = ModelHelper.faceFromIndex(i);
            if (!context.hasTransform() && context.isFaceCulled(cullFace)) continue;
            
            List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get(), context.getModelData(), context.getRenderType());
            
            for (BakedQuad q : quads) {
                context.getEmitter().fromVanilla(q, null, cullFace);
                context.getEmitter().emit();
            }
        }
    }
}
