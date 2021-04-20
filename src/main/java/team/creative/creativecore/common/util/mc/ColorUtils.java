package team.creative.creativecore.common.util.mc;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class ColorUtils {
    
    public static class Color {
        
        public int red;
        public int green;
        public int blue;
        public int alpha;
        
        public Color(int color) {
            this.red = getRed(color);
            this.green = getGreen(color);
            this.blue = getBlue(color);
            this.alpha = getAlpha(color);
        }
        
        public Color(Vector3i vec) {
            this.red = vec.getX();
            this.green = vec.getY();
            this.blue = vec.getZ();
            this.alpha = 255;
        }
        
        public Color(Vector3d vec) {
            this.red = (int) (vec.x * 255);
            this.green = (int) (vec.y * 255);
            this.blue = (int) (vec.z * 255);
            this.alpha = 255;
        }
        
        public Color(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = 255;
        }
        
        public Color(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
        
        public void glColor() {
            RenderSystem.color4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
        }
        
        public Vector3d toVec(ColorPart part) {
            return new Vector3d(red / 255D, green / 255D, blue / 255D);
        }
        
        public float getDecimal(ColorPart part) {
            return get(part) / 255F;
        }
        
        public int get(ColorPart part) {
            switch (part) {
            case RED:
                return red;
            case GREEN:
                return green;
            case BLUE:
                return blue;
            case ALPHA:
                return alpha;
            }
            return 0;
        }
        
        public void set(ColorPart part, int value) {
            switch (part) {
            case RED:
                this.red = value;
                break;
            case GREEN:
                this.green = value;
                break;
            case BLUE:
                this.blue = value;
                break;
            case ALPHA:
                this.alpha = value;
                break;
            }
        }
        
        public int toInt() {
            return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
        
        public boolean isWhite(int color) {
            return red == 255 && green == 255 && blue == 255;
        }
        
        public boolean isTransparent() {
            return alpha < 255;
        }
        
        public boolean isInvisible() {
            return alpha == 0;
        }
        
        public int blend(Color color) {
            return blend(color, 0.5F);
        }
        
        /** @param color
         *            color to blend with
         * @param ratio
         *            intensity of object color, the ratio of color parameter is 1 - ratio
         * @return */
        public int blend(Color color, float ratio) {
            if (ratio > 1f)
                ratio = 1f;
            else if (ratio < 0f)
                ratio = 0f;
            float iRatio = 1.0f - ratio;
            
            int a = (int) ((color.alpha * iRatio) + (alpha * ratio));
            int r = (int) ((color.red * iRatio) + (red * ratio));
            int g = (int) ((color.green * iRatio) + (green * ratio));
            int b = (int) ((color.blue * iRatio) + (blue * ratio));
            
            return a << 24 | r << 16 | g << 8 | b;
        }
        
    }
    
    public static enum ColorPart {
        RED(0xFF000000),
        GREEN(0x00FF0000),
        BLUE(0x0000FF00),
        ALPHA(0x000000FF);
        
        public final int code;
        
        private ColorPart(int code) {
            this.code = code;
        }
    }
    
    public static final int WHITE = -1;
    public static final int RED = -65536;
    public static final int GREEN = -16711936;
    public static final int BLUE = -16776961;
    public static final int YELLOW = -256;
    public static final int CYAN = 16711681;
    public static final int MAGENTA = -65281;
    public static final int BLACK = -16777216;
    
    public static int getAlpha(int color) {
        return color >> 24 & 255;
    }
    
    public static int getRed(int color) {
        return color >> 16 & 255;
    }
    
    public static int getGreen(int color) {
        return color >> 8 & 255;
    }
    
    public static int getBlue(int color) {
        return color & 255;
    }
    
    public static int toInt(int red, int green, int blue, int alpha) {
        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }
    
    public static int toInt(Vector3i color) {
        return (255 & 255) << 24 | (color.getX() & 255) << 16 | (color.getY() & 255) << 8 | color.getZ() & 255;
    }
    
    public static Vector3i toIntVec(int color) {
        float r = color >> 16 & 255;
        float g = color >> 8 & 255;
        float b = color & 255;
        return new Vector3i(r, g, b);
    }
    
    public static Vector3d toVec(int color) {
        float r = color >> 16 & 255;
        float g = color >> 8 & 255;
        float b = color & 255;
        return new Vector3d(r / 255F, g / 255F, b / 255F);
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
