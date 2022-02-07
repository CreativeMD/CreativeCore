package team.creative.creativecore.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
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
    
    public static void drawItemStackDecorations(PoseStack posestack, ItemStack stack) {
        if (!stack.isEmpty()) {
            int x = 0;
            int y = 0;
            if (stack.getCount() != 1l) {
                String s = String.valueOf(stack.getCount());
                posestack.translate(0.0D, 0.0D, 0 + 200.0F);
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                mc.font.drawInBatch(s, x + 19 - 2 - mc.font.width(s), y + 6 + 3, 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
                multibuffersource$buffersource.endBatch();
            }
            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = stack.getBarWidth();
                int j = stack.getBarColor();
                colorRect(posestack, bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                colorRect(posestack, bufferbuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
            
            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                colorRect(posestack, bufferbuilder1, x, y + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
            
        }
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
    
    public static void gradientRect(PoseStack pose, int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        gradientRect(pose.last().pose(), bufferbuilder, x, y, x2, y2, 0, colorFrom, colorTo);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
    public static void gradientRect(Matrix4f matrix, BufferBuilder builder, int x, int y, int x2, int y2, int z, int colorA, int colorB) {
        float f = (colorA >> 24 & 255) / 255.0F;
        float f1 = (colorA >> 16 & 255) / 255.0F;
        float f2 = (colorA >> 8 & 255) / 255.0F;
        float f3 = (colorA & 255) / 255.0F;
        float f4 = (colorB >> 24 & 255) / 255.0F;
        float f5 = (colorB >> 16 & 255) / 255.0F;
        float f6 = (colorB >> 8 & 255) / 255.0F;
        float f7 = (colorB & 255) / 255.0F;
        builder.vertex(matrix, x2, y, z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, x, y, z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, x, y2, z).color(f5, f6, f7, f4).endVertex();
        builder.vertex(matrix, x2, y2, z).color(f5, f6, f7, f4).endVertex();
    }
    
    public static void gradientMaskRect(PoseStack pose, int x, int y, int x2, int y2, int color, int mask) {
        gradientRect(pose, x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    public static void colorRect(PoseStack pose, BufferBuilder builder, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        Matrix4f mat = pose.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(mat, x, y, 0.0F).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, x, y + height, 0.0F).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, x + width, y + height, 0.0F).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, x + width, y, 0.0F).color(red, green, blue, alpha).endVertex();
        builder.end();
        BufferUploader.end(builder);
    }
    
    private static void textureRect(PoseStack pose, int x, int y, int width, int height, int z, float u, float v, int textureWidth, int textureHeight) {
        textureRect(pose, x, x + width, y, y + height, z, u, v, width, height, textureWidth, textureHeight);
    }
    
    public static void textureRect(PoseStack pose, int x, int y, int width, int height, float u, float v) {
        textureRect(pose, x, y, 0, width, height, u, v, 256, 256);
    }
    
    public static void textureRect(PoseStack pose, int x, int y, int width, int height, float u, float v, float u2, float v2) {
        textureRect(pose, x, y, 0, width, height, u, v, u2, v2, 256, 256);
    }
    
    private static void textureRect(PoseStack pose, int x, int x2, int y, int y2, int z, float u, float v, float u2, float v2, int textureWidth, int textureHeight) {
        drawTextureRect(pose.last().pose(), x, x2, y, y2, z, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight);
    }
    
    private static void textureRect(PoseStack pose, int x, int x2, int y, int y2, int z, float u, float v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        drawTextureRect(pose.last().pose(), x, x2, y, y2, z, u / textureWidth, (u + uWidth) / textureWidth, v / textureHeight, (v + vHeight) / textureHeight);
    }
    
    private static void drawTextureRect(Matrix4f matrix, int x, int x2, int y, int y2, int z, float u, float u2, float v, float v2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x, y2, z).uv(u, v2).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
        bufferbuilder.vertex(matrix, x2, y, z).uv(u2, v).endVertex();
        bufferbuilder.vertex(matrix, x, y, z).uv(u, v).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
    
}
