package team.creative.creativecore.common.gui.style.display;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;

public class DisplayTextureRepeat extends DisplayTexture {
    
    public int w;
    public int h;
    
    public DisplayTextureRepeat() {
        super();
    }
    
    public DisplayTextureRepeat(Identifier identifier, int u, int v, int width, int height) {
        super(identifier, u, v);
        this.w = width;
        this.h = height;
    }
    
    @Override
    public void render(GuiGraphics graphics, double x, double y, double width, double height) {
        int renderedX = 0;
        while (renderedX < (int) width) {
            int renderedY = 0;
            int renderedWidth = Math.min(w, (int) width - renderedX);
            while (renderedY < (int) height) {
                int renderedHeight = Math.min(h, (int) height - renderedY);
                ((CreativeGuiGraphics) graphics).textureRect(identifier, (int) x + renderedX, renderedY, (int) y + renderedWidth, renderedHeight, u, v, u + renderedWidth,
                    v + renderedHeight);
                renderedY += renderedHeight;
            }
            renderedX += renderedWidth;
        }
    }
    
}