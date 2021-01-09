package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextureDisplayStyle extends DisplayStyle {
    
    public ResourceLocation location;
    public int u;
    public int v;
    
    public TextureDisplayStyle(ResourceLocation location, int u, int v) {
        this.location = location;
        this.u = u;
        this.v = v;
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
        helper.drawTexturedModalRect(location, 0, 0, u, v, width, height);
        GlStateManager.popMatrix();
    }
    
    protected void colorize() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
}
