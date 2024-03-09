package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import team.creative.creativecore.client.render.GuiRenderHelper;

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
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GuiRenderHelper.textureRect(pose, (int) x, (int) y, (int) width, (int) height, u, v);
    }
    
}
