package team.creative.creativecore.common.util.type;

import org.joml.Vector3d;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class Color {
    
    public static final Color WHITE = new Color(ColorUtils.WHITE);
    public static final Color RED = new Color(ColorUtils.RED);
    public static final Color GREEN = new Color(ColorUtils.GREEN);
    public static final Color BLUE = new Color(ColorUtils.BLUE);
    public static final Color LIGHT_BLUE = new Color(ColorUtils.LIGHT_BLUE);
    public static final Color ORANGE = new Color(ColorUtils.ORANGE);
    public static final Color YELLOW = new Color(ColorUtils.YELLOW);
    public static final Color CYAN = new Color(ColorUtils.CYAN);
    public static final Color MAGENTA = new Color(ColorUtils.MAGENTA);
    public static final Color BLACK = new Color(ColorUtils.BLACK);
    
    protected short red;
    protected short green;
    protected short blue;
    protected short alpha;
    
    public Color() {
        this((short) 255, (short) 255, (short) 255, (short) 255);
    }
    
    public Color(Color color) {
        set(color);
    }
    
    public Color(int color) {
        this.alpha = (short) (color >> 24 & 255);
        this.red = (short) (color >> 16 & 255);
        this.green = (short) ((color >> 8 & 255));
        this.blue = (short) (color & 255);
    }
    
    public Color(float r, float g, float b) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
    }
    
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }
    
    public Color(short r, short g, short b) {
        this(r, g, b, (short) 255);
    }
    
    public Color(float r, float g, float b, float a) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
    }
    
    public Color(int r, int g, int b, int a) {
        this.red = (short) r;
        this.green = (short) g;
        this.blue = (short) b;
        this.alpha = (short) a;
    }
    
    public Color(short r, short g, short b, short a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }
    
    public Color(Vec3i vec) {
        this.red = (short) vec.getX();
        this.green = (short) vec.getY();
        this.blue = (short) vec.getZ();
        this.alpha = (short) 255;
    }
    
    public Color(Vector3d vec) {
        this.red = (short) (vec.x * 255);
        this.green = (short) (vec.y * 255);
        this.blue = (short) (vec.z * 255);
        this.alpha = (short) 255;
    }
    
    public void set(Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getAlpha();
    }
    
    public void set(ColorType type, int value) {
        set(type, (short) value);
    }
    
    public void set(ColorType type, short value) {
        switch (type) {
            case ALPHA:
                alpha = value;
                break;
            case RED:
                red = value;
                break;
            case GREEN:
                green = value;
                break;
            case BLUE:
                blue = value;
                break;
        }
    }
    
    public void setAlpha(int a) {
        this.alpha = (short) a;
    }
    
    public void setAlpha(short a) {
        this.alpha = a;
    }
    
    public void setRed(int r) {
        this.red = (short) r;
    }
    
    public void setRed(short r) {
        this.red = r;
    }
    
    public void setGreen(int g) {
        this.green = (short) g;
    }
    
    public void setGreen(short g) {
        this.green = g;
    }
    
    public void setBlue(int b) {
        this.blue = (short) b;
    }
    
    public void setBlue(short b) {
        this.blue = b;
    }
    
    public short get(ColorType type) {
        switch (type) {
            case ALPHA:
                return alpha;
            case RED:
                return red;
            case GREEN:
                return green;
            case BLUE:
                return blue;
            default:
                return 0;
        }
    }
    
    public short getAlpha() {
        return alpha;
    }
    
    public short getRed() {
        return red;
    }
    
    public short getGreen() {
        return green;
    }
    
    public short getBlue() {
        return blue;
    }
    
    public float getDecimal(ColorType type) {
        switch (type) {
            case ALPHA:
                return alpha / 255F;
            case RED:
                return red / 255F;
            case GREEN:
                return green / 255F;
            case BLUE:
                return blue / 255F;
            default:
                return 0;
        }
    }
    
    public float getAlphaDecimal() {
        return alpha / 255F;
    }
    
    public float getRedDecimal() {
        return red / 255F;
    }
    
    public float getGreenDecimal() {
        return green / 255F;
    }
    
    public float getBlueDecimal() {
        return blue / 255F;
    }
    
    public int toInt() {
        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }
    
    public boolean isDefault() {
        return red == 255 && green == 255 && blue == 255 && alpha == 255;
    }
    
    public boolean isWhite() {
        return red == 255 && green == 255 && blue == 255;
    }
    
    public boolean isTransparent() {
        return alpha < 255;
    }
    
    public boolean isInvisible() {
        return alpha == 0;
    }
    
    public void blend(Color color) {
        blend(color, 0.5F);
    }
    
    public void blend(Color color, float ratio) {
        if (ratio > 1f)
            ratio = 1f;
        else if (ratio < 0f)
            ratio = 0f;
        float iRatio = 1.0f - ratio;
        
        this.alpha = (short) ((alpha * iRatio) + (color.alpha * ratio));
        this.red = (short) ((red * iRatio) + (color.red * ratio));
        this.green = (short) ((green * iRatio) + (color.green * ratio));
        this.blue = (short) ((blue * iRatio) + (color.blue * ratio));
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void glColor() {
        RenderSystem.setShaderColor(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }
    
    public Vec3d toVec() {
        return new Vec3d(red / 255D, green / 255D, blue / 255D);
    }
    
    public static boolean isWhite(int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        return r == 255 && g == 255 && b == 255;
    }
    
    public static boolean isTransparent(int color) {
        int a = color >> 24 & 255;
        return a < 255;
    }
    
    public static boolean isInvisible(int color) {
        int a = color >> 24 & 255;
        return a == 0;
    }
    
    public static Color blend(Color color1, Color color2) {
        return blend(color1, color2, 0.5F);
    }
    
    public static Color blend(Color color1, Color color2, float ratio) {
        if (ratio > 1f)
            ratio = 1f;
        else if (ratio < 0f)
            ratio = 0f;
        float iRatio = 1.0f - ratio;
        
        short a = (short) ((color1.alpha * iRatio) + (color2.alpha * ratio));
        short r = (short) ((color1.red * iRatio) + (color2.red * ratio));
        short g = (short) ((color1.green * iRatio) + (color2.green * ratio));
        short b = (short) ((color1.blue * iRatio) + (color2.blue * ratio));
        
        return new Color(r, g, b, a);
    }
    
    public static int blend(int i1, int i2) {
        return blend(i1, i2, 0.5F);
    }
    
    public static int blend(int i1, int i2, float ratio) {
        if (ratio > 1f)
            ratio = 1f;
        else if (ratio < 0f)
            ratio = 0f;
        float iRatio = 1.0f - ratio;
        
        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);
        
        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);
        
        int a = (int) ((a1 * iRatio) + (a2 * ratio));
        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));
        
        return a << 24 | r << 16 | g << 8 | b;
    }
    
}
