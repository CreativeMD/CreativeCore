package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.render.GuiRenderHelper;

public class DisplayTextureRepeat extends DisplayTexture {
    
    public int w;
    public int h;
    
    public DisplayTextureRepeat() {
        super();
    }
    
    public DisplayTextureRepeat(ResourceLocation location, int u, int v, int width, int height) {
        super(location, u, v);
        this.w = width;
        this.h = height;
    }
    
    @Override
    public void render(PoseStack pose, double x, double y, double width, double height) {
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        int renderedX = 0;
        while (renderedX < (int) width) {
            int renderedY = 0;
            int renderedWidth = Math.min(w, (int) width - renderedX);
            while (renderedY < (int) height) {
                int renderedHeight = Math.min(h, (int) height - renderedY);
                GuiRenderHelper.textureRect(pose, (int) x + renderedX, renderedY, (int) y + renderedWidth, renderedHeight, u, v, u + renderedWidth, v + renderedHeight);
                renderedY += renderedHeight;
            }
            renderedX += renderedWidth;
        }
    }
    
}