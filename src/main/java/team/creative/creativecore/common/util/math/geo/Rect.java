package team.creative.creativecore.common.util.math.geo;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.base.Axis;

public class Rect {
    
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;
    
    public Rect(double x, double y, double x2, double y2) {
        this.minX = x;
        this.minY = y;
        this.maxX = x2;
        this.maxY = y2;
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public static Rect getScreenRect() {
        Minecraft mc = Minecraft.getInstance();
        return new Rect(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }
    
    public boolean intersects(Rect other) {
        return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY;
    }
    
    public Rect intersection(Rect other) {
        if (intersects(other)) {
            double x = Math.max(this.minX, other.minX);
            double y = Math.max(this.minY, other.minY);
            double x2 = Math.min(this.maxX, other.maxX);
            double y2 = Math.min(this.maxY, other.maxY);
            return new Rect(x, y, x2, y2);
        }
        return null;
    }
    
    public void move(double x, double y) {
        this.minX += x;
        this.maxX += x;
        this.minY += y;
        this.maxY += y;
    }
    
    public void shrink(double shrink) {
        minX += shrink;
        minY += shrink;
        maxX -= shrink;
        maxY -= shrink;
    }
    
    public void grow(double grow) {
        minX -= grow;
        minY -= grow;
        maxX += grow;
        maxY += grow;
    }
    
    public double getWidth() {
        return maxX - minX;
    }
    
    public double getHeight() {
        return maxY - minY;
    }
    
    public double getSize(Axis axis) {
        switch (axis) {
            case X:
                return getWidth();
            case Y:
                return getHeight();
            default:
                return 0;
        }
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void scissor() {
        Window window = Minecraft.getInstance().getWindow();
        double realMinX = minX * window.getGuiScale();
        double realMinY = window.getHeight() - (minY + getHeight()) * window.getGuiScale();
        double realMaxX = getWidth() * window.getGuiScale();
        double realMaxY = getHeight() * window.getGuiScale();
        
        RenderSystem.enableScissor((int) Math.floor(realMinX), (int) Math.floor(realMinY), (int) Math.ceil(realMaxX), (int) Math.ceil(realMaxY) + 1);
    }
    
    public Rect copy() {
        return new Rect(minX, minY, maxX, maxY);
    }
    
    public Rect child(double x, double y, double width, double height) {
        return new Rect(minX + x, minY + y, minX + x + width, minY + y + height);
    }
    
    public Rect child(Rect rect, double scale, double xOffset, double yOffset) {
        return new Rect(minX + (rect.minX + xOffset) * scale, minY + (rect.minY + yOffset) * scale, minX + (rect.maxX + xOffset) * scale, minY + (rect.maxY + yOffset) * scale);
    }
    
    public boolean inside(double x, double y) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY;
    }
    
    @Override
    public String toString() {
        return "[" + minX + "," + minY + "," + maxX + "," + maxY + "]";
    }
    
    public void scale(double scale) {
        minX *= scale;
        minY *= scale;
        maxX *= scale;
        maxY *= scale;
    }
    
}
