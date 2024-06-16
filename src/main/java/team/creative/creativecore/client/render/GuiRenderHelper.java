package team.creative.creativecore.client.render;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.mc.ColorUtils;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
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
        Matrix4fStack matrix = RenderSystem.getModelViewStack();
        matrix.pushMatrix();
        matrix.mul(mat.last().pose());
        matrix.translate(0, 0, 100);
        matrix.translate(8, 8, 8);
        matrix.mul((new Matrix4f()).scaling(1, -1, 1));
        matrix.scale(16, 16, 16);
        
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        BakedModel bakedmodel = renderer.getModel(stack, null, null, 0);
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag)
            Lighting.setupForFlatItems();
        renderer.render(stack, ItemDisplayContext.GUI, false, new PoseStack(), multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag)
            Lighting.setupFor3DItems();
        
        matrix.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }
    
    public static void drawStringCentered(GuiGraphics graphics, String text, float width, float height, int color, boolean shadow) {
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
                text = builder + "...";
            }
        } ;
        mc.font.drawInBatch(text, width / 2 - mc.font.width(text) / 2, height / 2 - mc.font.lineHeight / 2, ColorUtils.WHITE, shadow, graphics.pose().last().pose(), graphics
                .bufferSource(), DisplayMode.NORMAL, 0, 15728880);
    }
    
    public static void horizontalGradientRect(GuiGraphics graphics, int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        var matrix = graphics.pose().last().pose();
        int z = 0;
        consumer.addVertex(matrix, x2, y, z).setColor(colorTo);
        consumer.addVertex(matrix, x, y, z).setColor(colorFrom);
        consumer.addVertex(matrix, x, y2, z).setColor(colorFrom);
        consumer.addVertex(matrix, x2, y2, z).setColor(colorTo);
    }
    
    public static void verticalGradientRect(GuiGraphics graphics, int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        var matrix = graphics.pose().last().pose();
        int z = 0;
        consumer.addVertex(matrix, x2, y, z).setColor(colorFrom);
        consumer.addVertex(matrix, x, y, z).setColor(colorFrom);
        consumer.addVertex(matrix, x, y2, z).setColor(colorTo);
        consumer.addVertex(matrix, x2, y2, z).setColor(colorTo);
    }
    
    public static void horizontalGradientMaskRect(GuiGraphics graphics, int x, int y, int x2, int y2, int color, int mask) {
        horizontalGradientRect(graphics, x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    public static void colorRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        var matrix = graphics.pose().last().pose();
        int z = 0;
        consumer.addVertex(matrix, x, y, z).setColor(color);
        consumer.addVertex(matrix, x, y + height, z).setColor(color);
        consumer.addVertex(matrix, x + width, y + height, z).setColor(color);
        consumer.addVertex(matrix, x + width, y, z).setColor(color);
    }
    
    private static void textureRect(GuiGraphics graphics, int x, int y, int z, int width, int height, float u, float v, int textureWidth, int textureHeight) {
        textureRect(graphics, x, x + width, y, y + height, z, u, v, width, height, textureWidth, textureHeight);
    }
    
    public static void textureRect(GuiGraphics graphics, int x, int y, int width, int height, float u, float v) {
        textureRect(graphics, x, y, 0, width, height, u, v, 256, 256);
    }
    
    public static void textureRect(GuiGraphics graphics, int x, int y, int width, int height, float u, float v, float u2, float v2) {
        textureRect(graphics, x, x + width, y, y + height, 0, u, v, u2, v2, 256, 256);
    }
    
    private static void textureRect(GuiGraphics graphics, int x, int x2, int y, int y2, int z, float u, float v, float u2, float v2, int textureWidth, int textureHeight) {
        drawTextureRect(graphics, x, x2, y, y2, z, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight);
    }
    
    private static void textureRect(GuiGraphics graphics, int x, int x2, int y, int y2, int z, float u, float v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        drawTextureRect(graphics, x, x2, y, y2, z, u / textureWidth, (u + uWidth) / textureWidth, v / textureHeight, (v + vHeight) / textureHeight);
    }
    
    private static void drawTextureRect(GuiGraphics graphics, int x, int x2, int y, int y2, int z, float u, float u2, float v, float v2) {
        var matrix = graphics.pose().last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, y2, z).setUv(u, v2);
        bufferbuilder.addVertex(matrix, x2, y2, z).setUv(u2, v2);
        bufferbuilder.addVertex(matrix, x2, y, z).setUv(u2, v);
        bufferbuilder.addVertex(matrix, x, y, z).setUv(u, v);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
    
}
