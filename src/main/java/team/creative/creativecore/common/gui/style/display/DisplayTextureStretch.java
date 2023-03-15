package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

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
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GuiRenderHelper.textureRect(pose, (int) x, (int) y, (int) width, (int) height, u, v, u + w, v + h);
        RenderSystem.disableBlend();
    }
    
}