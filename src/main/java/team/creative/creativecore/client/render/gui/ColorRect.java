package team.creative.creativecore.client.render.gui;

import javax.annotation.Nullable;

import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record ColorRect(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int color, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
    
    @Override
    public void buildVertices(VertexConsumer consumer, float z) {
        consumer.addVertexWith2DPose(this.pose, this.x1, this.y0, z).setColor(this.color);
        consumer.addVertexWith2DPose(this.pose, this.x0, this.y0, z).setColor(this.color);
        consumer.addVertexWith2DPose(this.pose, this.x0, this.y1, z).setColor(this.color);
        consumer.addVertexWith2DPose(this.pose, this.x1, this.y1, z).setColor(this.color);
    }
    
    @Override
    public ScreenRectangle bounds() {
        return null;
    }
    
}
