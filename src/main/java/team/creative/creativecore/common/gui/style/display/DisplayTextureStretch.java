package team.creative.creativecore.common.gui.style.display;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;

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
    public void render(GuiGraphics graphics, double x, double y, double width, double height) {
        ((CreativeGuiGraphics) graphics).textureRect(location, (int) x, (int) y, (int) width, (int) height, u, v, u + w, v + h);
    }
    
}