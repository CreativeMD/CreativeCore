package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class DisplayTexture extends StyleDisplay {
    
    public ResourceLocation location;
    public int u;
    public int v;
    
    public DisplayTexture() {
        this(new ResourceLocation("missing"), 0, 0);
    }
    
    public DisplayTexture(ResourceLocation location, int u, int v) {
        this.location = location;
        this.u = u;
        this.v = v;
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
        
        GuiComponent.blit(pose, 0, 0, 0, u, v, (int) width, (int) height, 256, 256);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
}
