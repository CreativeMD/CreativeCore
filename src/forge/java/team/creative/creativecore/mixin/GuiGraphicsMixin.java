package team.creative.creativecore.mixin;

import javax.annotation.Nullable;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.client.render.gui.ColorRect;
import team.creative.creativecore.client.render.gui.ColorRectF;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.client.render.gui.HorizontalGradientRect;
import team.creative.creativecore.client.render.gui.HorizontalGradientRectF;
import team.creative.creativecore.client.render.gui.VerticalGradientRect;
import team.creative.creativecore.client.render.gui.VerticalGradientRectF;
import team.creative.creativecore.common.util.mc.ColorUtils;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements CreativeGuiGraphics {
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Shadow
    @Final
    private Matrix3x2fStack pose;
    
    @Shadow
    @Final
    private GuiRenderState guiRenderState;
    
    @Unique
    private GuiGraphics as() {
        return (GuiGraphics) (Object) this;
    }
    
    @Override
    public void drawStringCentered(String text, float width, float height, int color, boolean shadow) {
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
        as().drawString(minecraft.font, text, (int) (width / 2 - minecraft.font.width(text) / 2), (int) (height / 2 - minecraft.font.lineHeight / 2), ColorUtils.WHITE, shadow);
    }
    
    @Override
    public void drawString(FormattedCharSequence text, int x, int y, int color, int shadowColor, boolean shadow) {
        if (ARGB.alpha(color) != 0)
            this.guiRenderState.submitText(new GuiTextRenderState(minecraft.font, text, new Matrix3x2f(this.pose), x, y, color, shadowColor, shadow, as().peekScissorStack()));
    }
    
    @Override
    public void horizontalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        guiRenderState.submitGuiElement(new HorizontalGradientRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x2, y2, colorFrom, colorTo, as().peekScissorStack()));
    }
    
    @Override
    public void horizontalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo) {
        guiRenderState.submitGuiElement(new HorizontalGradientRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x2, y2, colorFrom, colorTo, as()
                .peekScissorStack()));
    }
    
    @Override
    public void verticalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo) {
        guiRenderState.submitGuiElement(new VerticalGradientRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x2, y2, colorFrom, colorTo, as().peekScissorStack()));
    }
    
    @Override
    public void verticalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo) {
        guiRenderState.submitGuiElement(new VerticalGradientRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x2, y2, colorFrom, colorTo, as().peekScissorStack()));
    }
    
    @Override
    public void horizontalGradientMaskRect(int x, int y, int x2, int y2, int color, int mask) {
        horizontalGradientRect(x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    @Override
    public void horizontalGradientMaskRect(float x, float y, float x2, float y2, int color, int mask) {
        horizontalGradientRect(x, y, x2, y2, (color & ~mask) | 0xFF000000, color | 0xFF000000 | mask);
    }
    
    @Override
    public void colorRect(int x, int y, int width, int height, int color) {
        guiRenderState.submitGuiElement(new ColorRect(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x + width, y + height, color, as().peekScissorStack()));
    }
    
    @Override
    public void colorRect(float x, float y, float width, float height, int color) {
        guiRenderState.submitGuiElement(new ColorRectF(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x + width, y + height, color, as().peekScissorStack()));
    }
    
    @Override
    public void textureRect(ResourceLocation location, int x, int y, int width, int height, float u, float v) {
        int textureWidth = 256;
        int textureHeight = 256;
        drawTextureRect(location, x, x + width, y, y + height, 0, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight);
    }
    
    @Override
    public void textureRect(ResourceLocation location, float x, float y, float width, float height, float u, float v) {
        int textureWidth = 256;
        int textureHeight = 256;
        drawTextureRect(location, x, x + width, y, y + height, 0, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight);
    }
    
    @Override
    public void textureRect(ResourceLocation location, int x, int y, int width, int height, float u, float v, float u2, float v2) {
        int textureWidth = 256;
        int textureHeight = 256;
        drawTextureRect(location, x, x + width, y, y + height, 0, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight);
    }
    
    @Override
    public void textureRect(ResourceLocation location, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        int textureWidth = 256;
        int textureHeight = 256;
        drawTextureRect(location, x, x + width, y, y + height, 0, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight);
    }
    
    @Override
    public void textureRectColor(ResourceLocation location, int x, int y, int width, int height, float u, float v, float u2, float v2, int color) {
        int textureWidth = 256;
        int textureHeight = 256;
        drawTextureRect(location, x, x + width, y, y + height, 0, u / textureWidth, u2 / textureWidth, v / textureHeight, v2 / textureHeight);
    }
    
    @Override
    public void renderItemDecorations(ItemStack stack, int x, int y) {
        as().renderItemDecorations(minecraft.font, stack, x, y);
    }
    
    @Override
    public void renderItemDecorations(ItemStack stack, int x, int y, @Nullable String text) {
        as().renderItemDecorations(minecraft.font, stack, x, y, text);
    }
    
    @Override
    public Font getFont() {
        return minecraft.font;
    }
    
    private void drawTextureRect(ResourceLocation location, int x, int x2, int y, int y2, int z, float u, float u2, float v, float v2) {
        as().blit(location, x, x2, y, y2, u, u2, v, v2);
    }
    
    private void drawTextureRect(ResourceLocation location, float x, float x2, float y, float y2, float z, float u, float u2, float v, float v2) {
        as().blit(location, (int) x, (int) x2, (int) y, (int) y2, u, u2, v, v2);
    }
    
}
