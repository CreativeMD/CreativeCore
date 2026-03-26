package team.creative.creativecore.client.render.gui;

import javax.annotation.Nullable;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.common.util.mc.ColorUtils;

@Environment(EnvType.CLIENT)
public interface CreativeGuiGraphics {
    
    public static CreativeGuiGraphics as(GuiGraphicsExtractor graphics) {
        return (CreativeGuiGraphics) graphics;
    }
    
    public default void drawStringCentered(String text, float width, float height, int color, boolean shadow) {
        var minecraft = minecraft();
        int textWidth = minecraft.font.width(text);
        if (textWidth > width) {
            int dotWith = minecraft.font.width("...");
            if (textWidth > dotWith) {
                StringBuilder builder = new StringBuilder();
                textWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    int charWidth = minecraft.font.width("" + text.charAt(i));
                    if (charWidth + textWidth + dotWith < width) {
                        builder.append(text.charAt(i));
                        textWidth += charWidth;
                    } else
                        break;
                }
                text = builder + "...";
            }
        } ;
        as().text(minecraft.font, text, (int) (width / 2 - minecraft.font.width(text) / 2), (int) (height / 2 - minecraft.font.lineHeight / 2), ColorUtils.WHITE, shadow);
    }
    
    public default void drawString(FormattedCharSequence text, int x, int y, int color, int shadowColor, boolean shadow) {
        if (ARGB.alpha(color) != 0)
            guiRenderState().addText(new GuiTextRenderState(font(), text, new Matrix3x2f(pose()), x, y, color, shadowColor, shadow, true, peekScissor()));
    }
    
    public default void horizontalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        guiRenderState().addGuiElement(new HorizontalGradientRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x2, y2, colorFrom, colorTo, peekScissor()));
    }
    
    public default void horizontalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo) {
        guiRenderState().addGuiElement(new HorizontalGradientRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x2, y2, colorFrom, colorTo, peekScissor()));
    }
    
    public default void verticalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        guiRenderState().addGuiElement(new VerticalGradientRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x2, y2, colorFrom, colorTo, peekScissor()));
    }
    
    public default void verticalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo) {
        guiRenderState().addGuiElement(new VerticalGradientRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x2, y2, colorFrom, colorTo, peekScissor()));
    }
    
    public default void horizontalGradientMaskRect(int x, int y, int x2, int y2, int color, int mask) {
        horizontalGradientRect(x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    public default void horizontalGradientMaskRect(float x, float y, float x2, float y2, int color, int mask) {
        horizontalGradientRect(x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    public default void colorRect(int x, int y, int width, int height, int color) {
        guiRenderState().addGuiElement(new ColorRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x + width, y + height, color, peekScissor()));
    }
    
    public default void colorRect(float x, float y, float width, float height, int color) {
        guiRenderState().addGuiElement(new ColorRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose(), x, y, x + width, y + height, color, peekScissor()));
    }
    
    public default void textureRect(Identifier location, int x, int y, int width, int height, float u, float v) {
        int textureWidth = 256;
        int textureHeight = 256;
        blit(RenderPipelines.GUI_TEXTURED, location, x, x + width, y, y + height, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight,
            -1);
    }
    
    public default void textureRect(Identifier location, float x, float y, float width, float height, float u, float v) {
        int textureWidth = 256;
        int textureHeight = 256;
        blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) (x + width), (int) y, (int) (y + height), u / textureWidth, (u + width) / textureWidth, v / textureHeight,
            (v + height) / textureHeight, -1);
    }
    
    public default void textureRect(Identifier location, int x, int y, int width, int height, float u, float v, float u2, float v2) {
        int textureWidth = 256;
        int textureHeight = 256;
        blit(RenderPipelines.GUI_TEXTURED, location, x, x + width, y, y + height, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight, -1);
    }
    
    public default void textureRect(Identifier location, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        int textureWidth = 256;
        int textureHeight = 256;
        blit(RenderPipelines.GUI_TEXTURED, location, (int) x, (int) (x + width), (int) y, (int) (y + height), u / textureWidth, u2 / textureWidth, v / textureHeight,
            v2 / textureHeight, -1);
    }
    
    public default void textureRectColor(Identifier location, int x, int y, int width, int height, float u, float v, float u2, float v2, int color) {
        int textureWidth = 256;
        int textureHeight = 256;
        blit(RenderPipelines.GUI_TEXTURED, location, x, x + width, y, y + height, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight, color);
    }
    
    public default void textureRectStrechted(Identifier location, int x, int y, int width, int height) {
        blit(RenderPipelines.GUI_TEXTURED, location, x, x + width, y, y + height, 0, 1, 0, 1, -1);
    }
    
    public default void textureRectColorStretched(Identifier location, int x, int y, int width, int height, int color) {
        blit(RenderPipelines.GUI_TEXTURED, location, x, x + width, y, y + height, 0, 1, 0, 1, color);
    }
    
    public default void renderItemDecorations(ItemStack stack, int x, int y) {
        as().itemDecorations(font(), stack, x, y);
    }
    
    public default void renderItemDecorations(ItemStack stack, int x, int y, @Nullable String text) {
        as().itemDecorations(font(), stack, x, y, text);
    }
    
    public void blit(RenderPipeline renderPipeline, Identifier resourceLocation, int x0, int x1, int y0, int y1, float u, float u2, float v, float v2, int color);
    
    public GuiGraphicsExtractor as();
    
    public Minecraft minecraft();
    
    public Matrix3x2fStack pose();
    
    public Font font();
    
    public GuiRenderState guiRenderState();
    
    public ScreenRectangle peekScissor();
    
    public void setOverrideScissor(@Nullable ScreenRectangle rect);
    
    public void clearOverrideScissor();
    
}
