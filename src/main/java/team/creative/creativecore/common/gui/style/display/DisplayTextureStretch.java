package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.render.GuiRenderHelper;

public class DisplayTextureStretch extends DisplayTexture {
    
    public int w;
    public int h;
    
    public DisplayTextureStretch() {
        super();
    }
    
    public DisplayTextureStretch(ResourceLocation location, int u, int v, int width, int height) {
        super(location, u, v);
        this.w = width;
        this.h = height;
    }
    
    @Override
    public void render(PoseStack pose, double x, double y, double width, double height) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableTexture();
        
        GuiRenderHelper.textureRect(pose, 0, 0, (int) width, (int) height, u, v, w, h);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
}