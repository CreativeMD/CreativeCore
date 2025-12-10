package team.creative.creativecore.common.gui.style.display;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;

public class DisplayTexture extends StyleDisplay {
    
    public Identifier identifier;
    public int u;
    public int v;
    
    public DisplayTexture() {
        this(Identifier.withDefaultNamespace("missing"), 0, 0);
    }
    
    public DisplayTexture(Identifier identifier, int u, int v) {
        this.identifier = identifier;
        this.u = u;
        this.v = v;
    }
    
    @Override
    public void render(GuiGraphics graphics, double x, double y, double width, double height) {
        ((CreativeGuiGraphics) graphics).textureRect(identifier, (int) x, (int) y, (int) width, (int) height, u, v);
    }
    
}
