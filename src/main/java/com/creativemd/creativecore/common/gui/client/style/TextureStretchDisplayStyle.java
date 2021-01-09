package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextureStretchDisplayStyle extends TextureDisplayStyle {
    
    public int h;
    public int w;
    
    public TextureStretchDisplayStyle(ResourceLocation location, int u, int v, int width, int height) {
        super(location, u, v);
        this.h = height;
        this.w = width;
    }
    
    @Override
    public void renderStyle(GuiRenderHelper helper, int width, int height) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        colorize();
        GlStateManager
            .tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        // GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        helper.drawTexturedModalRect(location, 0, 0, u, v, width, height, w, h);
        GlStateManager.popMatrix();
    }
    
}
