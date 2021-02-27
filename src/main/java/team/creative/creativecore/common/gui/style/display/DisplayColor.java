package team.creative.creativecore.common.gui.style.display;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class DisplayColor extends StyleDisplay {
    
    public float alpha;
    public float red;
    public float green;
    public float blue;
    
    public DisplayColor() {
        this(1, 1, 1, 1);
    }
    
    public DisplayColor(float r, float g, float b, float a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }
    
    @Override
    protected void render(Matrix4f mat, double x, double y, double width, double height) {
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(mat, (float) (x + width), (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(mat, (float) x, (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(mat, (float) x, (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(mat, (float) (x + width), (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
}
