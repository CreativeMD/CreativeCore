package team.creative.creativecore.common.gui.style.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.type.Color;

public class DisplayColor extends StyleDisplay {
    
    public float alpha;
    public float red;
    public float green;
    public float blue;
    
    public DisplayColor() {
        this(1, 1, 1, 1);
    }
    
    public DisplayColor(int color) {
        this(ColorUtils.redF(color), ColorUtils.greenF(color), ColorUtils.blueF(color), ColorUtils.alphaF(color));
    }
    
    public DisplayColor(float r, float g, float b, float a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }
    
    public DisplayColor(Color color) {
        this(color.getRedDecimal(), color.getGreenDecimal(), color.getBlueDecimal(), color.getAlphaDecimal());
    }
    
    @Override
    public void render(PoseStack pose, double x, double y, double width, double height) {
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        Matrix4f mat = pose.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, (float) (x + width), (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) x, (float) y, 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) x, (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(mat, (float) (x + width), (float) (y + height), 0).color(red, green, blue, alpha).endVertex();
        tessellator.end();
        
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    
}
