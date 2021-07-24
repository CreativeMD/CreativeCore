package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;

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
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //RenderSystem.shadeModel(GL11.GL_SMOOTH);
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, (float) (x + width), (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) x, (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) x, (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) (x + width), (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
        
        //RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
}
