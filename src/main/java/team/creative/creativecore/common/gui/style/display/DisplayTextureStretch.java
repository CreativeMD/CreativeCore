package team.creative.creativecore.common.gui.style.display;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;

public class DisplayTextureStretch extends DisplayTexture {
    
    public int w;
    public int h;
    
    public DisplayTextureStretch() {
        super();
    }
    
    public DisplayTextureStretch(Identifier identifier, int u, int v, int width, int height) {
        super(identifier, u, v);
        this.w = width;
        this.h = height;
    }
    
    @Override
    public void render(GuiGraphicsExtractor graphics, double x, double y, double width, double height) {
        ((CreativeGuiGraphics) graphics).textureRect(identifier, (int) x, (int) y, (int) width, (int) height, u, v, u + w, v + h);
    }
    
}