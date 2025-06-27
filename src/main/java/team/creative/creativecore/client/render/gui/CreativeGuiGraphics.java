package team.creative.creativecore.client.render.gui;

import javax.annotation.Nullable;

import org.joml.Matrix3x2f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
public interface CreativeGuiGraphics {
    
    public static CreativeGuiGraphics as(GuiGraphics graphics) {
        return (CreativeGuiGraphics) graphics;
    }
    
    @Nullable
    public static ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle rect) {
        ScreenRectangle screenrectangle = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return rect != null ? rect.intersection(screenrectangle) : screenrectangle;
    }
    
    @Nullable
    public static ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRectangle rect) {
        ScreenRectangle screenrectangle = new ScreenRectangle((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformMaxBounds(pose);
        return rect != null ? rect.intersection(screenrectangle) : screenrectangle;
    }
    
    public void drawStringCentered(String text, float width, float height, int color, boolean shadow);
    
    public void drawString(FormattedCharSequence text, int x, int y, int color, int shadowColor, boolean shadow);
    
    public void horizontalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo);
    
    public void horizontalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo);
    
    public void verticalGradientRect(int x, int y, int x2, int y2, int colorFrom, int colorTo);
    
    public void verticalGradientRect(float x, float y, float x2, float y2, int colorFrom, int colorTo);
    
    public void horizontalGradientMaskRect(int x, int y, int x2, int y2, int color, int mask);
    
    public void horizontalGradientMaskRect(float x, float y, float x2, float y2, int color, int mask);
    
    public void colorRect(int x, int y, int width, int height, int color);
    
    public void colorRect(float x, float y, float width, float height, int color);
    
    public void textureRect(ResourceLocation location, int x, int y, int width, int height, float u, float v);
    
    public void textureRect(ResourceLocation location, float x, float y, float width, float height, float u, float v);
    
    public void textureRect(ResourceLocation location, int x, int y, int width, int height, float u, float v, float u2, float v2);
    
    public void textureRect(ResourceLocation location, float x, float y, float width, float height, float u, float v, float u2, float v2);
    
    public void textureRectColor(ResourceLocation location, int x, int y, int width, int height, float u, float v, float u2, float v2, int color);
    
    public void renderItemDecorations(ItemStack stack, int x, int y);
    
    public void renderItemDecorations(ItemStack stack, int x, int y, @Nullable String text);
    
    public Font getFont();
    
}
