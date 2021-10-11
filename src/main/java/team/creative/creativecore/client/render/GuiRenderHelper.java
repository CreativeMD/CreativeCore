package team.creative.creativecore.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class GuiRenderHelper {
    
    private static final Minecraft mc = Minecraft.getInstance();
    
    public static Font getFont() {
        return mc.font;
    }
    
    public static void drawItemStack(PoseStack mat, ItemStack stack, float alpha) {
        ItemRenderer renderer = mc.getItemRenderer();
        mc.getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        PoseStack matrix = RenderSystem.getModelViewStack();
        matrix.pushPose();
        matrix.mulPoseMatrix(mat.last().pose());
        matrix.translate(0, 0, 100.0F + renderer.blitOffset);
        matrix.translate(8.0D, 8.0D, 0.0D);
        matrix.scale(1.0F, -1.0F, 1.0F);
        matrix.scale(16.0F, 16.0F, 16.0F);
        
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        BakedModel bakedmodel = renderer.getModel(stack, (Level) null, (LivingEntity) null, 0);
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag)
            Lighting.setupForFlatItems();
        renderer.render(stack, ItemTransforms.TransformType.GUI, false, new PoseStack(), multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag)
            Lighting.setupFor3DItems();
        
        matrix.popPose();
        RenderSystem.applyModelViewMatrix();
    }
    
    public static void drawStringCentered(PoseStack stack, String text, float width, float height, int color, boolean shadow) {
        int textWidth = mc.font.width(text);
        if (textWidth > width) {
            int dotWith = mc.font.width("...");
            if (textWidth > dotWith) {
                StringBuilder builder = new StringBuilder();
                textWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    int charWidth = mc.font.width("" + text.charAt(i));
                    if (charWidth + textWidth + dotWith < width) {
                        builder.append(text.charAt(i));
                        textWidth += charWidth;
                    } else
                        break;
                }
                text = builder.toString() + "...";
            }
        }
        mc.font.drawShadow(stack, text, width / 2 - mc.font.width(text) / 2, height / 2 - mc.font.lineHeight / 2, color);
    }
    
    public static void fillGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        fillGradient(matrixStack.last().pose(), bufferbuilder, x1, y1, x2, y2, 0, colorFrom, colorTo);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
    public static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
        float f = (colorA >> 24 & 255) / 255.0F;
        float f1 = (colorA >> 16 & 255) / 255.0F;
        float f2 = (colorA >> 8 & 255) / 255.0F;
        float f3 = (colorA & 255) / 255.0F;
        float f4 = (colorB >> 24 & 255) / 255.0F;
        float f5 = (colorB >> 16 & 255) / 255.0F;
        float f6 = (colorB >> 8 & 255) / 255.0F;
        float f7 = (colorB & 255) / 255.0F;
        builder.vertex(matrix, x2, y1, z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, x1, y1, z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, x1, y2, z).color(f5, f6, f7, f4).endVertex();
        builder.vertex(matrix, x2, y2, z).color(f5, f6, f7, f4).endVertex();
    }
    
}
