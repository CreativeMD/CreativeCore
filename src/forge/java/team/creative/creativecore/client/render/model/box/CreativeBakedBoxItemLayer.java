package team.creative.creativecore.client.render.model.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import team.creative.creativecore.client.render.box.QuadGeneratorContext;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.client.render.model.CreativeBakedQuad;
import team.creative.creativecore.client.render.model.layer.CreativeBakedItemLayer;
import team.creative.creativecore.common.util.math.base.Facing;
import team.creative.creativecore.common.util.mc.ColorUtils;

public record CreativeBakedBoxItemLayer(ItemModelBox model, BakedModel block) implements CreativeBakedItemLayer {
    
    public static Minecraft mc = Minecraft.getInstance();
    private static final ThreadLocal<QuadGeneratorContext> QUAD_CONTEXT = ThreadLocal.withInitial(QuadGeneratorContext::new);
    
    public static List<BakedQuad> compileBoxes(List<? extends RenderBox> boxes, Facing side, RenderType layer, RandomSource rand, boolean item, List<BakedQuad> baked) {
        if (side == null)
            return Collections.EMPTY_LIST;
        
        for (int i = 0; i < boxes.size(); i++) {
            RenderBox box = boxes.get(i);
            
            if (!box.shouldRenderFace(side))
                continue;
            
            BlockState state = Blocks.AIR.defaultBlockState();
            if (box.state != null)
                state = box.state;
            
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
            
            int defaultColor = ColorUtils.WHITE;
            if (item)
                defaultColor = mc.getItemColors().getColor(new ItemStack(state.getBlock()), defaultColor);
            
            boolean fusion = blockModel.getClass().getPackageName().startsWith("com.supermartijn642.fusion.");
            
            QuadGeneratorContext context = QUAD_CONTEXT.get();
            baked.addAll(box.getBakedQuad(context, null, null, box.getOffset(), state, blockModel, ModelData.EMPTY, side, fusion ? null : layer, rand, true, defaultColor));
            context.clear();
        }
        for (BakedQuad quad : baked)
            if (quad instanceof CreativeBakedQuad c)
                c.updateAlpha();
            
        return baked;
    }
    
    @Override
    public CreativeBakedItemLayer resolve(ItemStack stack) {
        var resolved = model.resolve(stack);
        if (resolved != null)
            return new CreativeBakedItemLayerWrapper(resolved);
        return this;
    }
    
    @Override
    public void render(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
            int packedOverlay) {
        pose.pushPose();
        
        pose.translate(0, 0, 0.5);
        
        block.applyTransform(context, pose, left);
        pose.translate(-0.5F, -0.5F, -0.5F);
        
        renderLayer(renderer, stack, context, left, pose, buffer, packedLight, packedOverlay, false);
        if (model.hasTranslucentLayer(stack))
            renderLayer(renderer, stack, context, left, pose, buffer, packedLight, packedOverlay, true);
        
        pose.popPose();
    }
    
    public void renderLayer(ItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean left, PoseStack pose, MultiBufferSource buffer, int packedLight,
            int packedOverlay, boolean translucent) {
        RenderType layer = translucent ? RenderTypeHelper.getEntityRenderType(RenderType.translucent(), true) : Sheets.cutoutBlockSheet();
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, layer, true, stack.hasFoil());
        renderModelLists(renderer, stack, layer, packedLight, packedOverlay, pose, vertexconsumer);
    }
    
    public void renderModelLists(ItemRenderer renderer, ItemStack stack, RenderType layer, int packedLight, int packedOverlay, PoseStack pose, VertexConsumer consumer) {
        RandomSource randomsource = RandomSource.create();
        randomsource.setSeed(42L);
        renderer.renderQuadList(pose, consumer, getBakedQuads(stack, layer, randomsource, layer != Sheets.cutoutBlockSheet()), stack, packedLight, packedOverlay);
    }
    
    public List<BakedQuad> getBakedQuads(ItemStack stack, RenderType layer, RandomSource rand, boolean translucent) {
        List<BakedQuad> cached = model.getCachedModel(translucent, stack, false);
        if (cached != null)
            return cached;
        List<? extends RenderBox> boxes = model.getBoxes(stack, translucent);
        if (boxes != null) {
            cached = new ArrayList<>();
            for (int i = 0; i < Facing.VALUES.length; i++)
                compileBoxes(boxes, Facing.VALUES[i], layer, rand, true, cached);
            model.saveCachedModel(translucent, cached, stack, false);
            return cached;
        }
        
        return Collections.EMPTY_LIST;
    }
    
}
