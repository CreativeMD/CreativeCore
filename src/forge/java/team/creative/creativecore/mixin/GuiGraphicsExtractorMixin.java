package team.creative.creativecore.mixin;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.resources.Identifier;
import team.creative.creativecore.client.render.ScissorStackExtender;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.reflection.ReflectionHelper;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin implements CreativeGuiGraphics {
    
    private static final Field SCISSOR_STACK_FIELD = ReflectionHelper.findFieldByType(GuiGraphicsExtractor.class, "net.minecraft.client.gui.GuiGraphicsExtractor$ScissorStack");
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Shadow
    @Final
    private Matrix3x2fStack pose;
    
    @Shadow
    @Final
    private GuiRenderState guiRenderState;
    
    @Override
    @Unique
    public GuiGraphicsExtractor as() {
        return (GuiGraphicsExtractor) (Object) this;
    }
    
    @Override
    public Font font() {
        return minecraft.font;
    }
    
    @Override
    public Minecraft minecraft() {
        return minecraft;
    }
    
    @WrapOperation(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/state/gui/GuiRenderState;II)V", require = 1, at = @At(value = "NEW",
            target = "(I)Lorg/joml/Matrix3x2fStack;"))
    private static Matrix3x2fStack modifyStackSize(int stacksize, Operation<Matrix3x2fStack> op) {
        return new Matrix3x2fStack(64);
    }
    
    @Override
    public void setOverrideScissor(@Nullable ScreenRectangle rect) {
        try {
            ((ScissorStackExtender) SCISSOR_STACK_FIELD.get(this)).setOverrideScissor(rect);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void clearOverrideScissor() {
        try {
            ((ScissorStackExtender) SCISSOR_STACK_FIELD.get(this)).clearOverrideScissor();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public ScreenRectangle peekScissor() {
        return as().peekScissorStack();
    }
    
    @Override
    public Matrix3x2fStack pose() {
        return pose;
    }
    
    @Override
    public GuiRenderState guiRenderState() {
        return guiRenderState;
    }
    
    @Override
    public void blit(RenderPipeline renderPipeline, Identifier resourceLocation, int x0, int x1, int y0, int y1, float u, float u2, float v, float v2, int color) {
        innerBlit(renderPipeline, resourceLocation, x0, x1, y0, y1, u, u2, v, v2, color);
    }
    
    @Shadow
    public void innerBlit(RenderPipeline renderPipeline, Identifier resourceLocation, int x0, int x1, int y0, int y1, float u, float u2, float v, float v2, int color) {
        throw new UnsupportedOperationException();
    }
    
}
