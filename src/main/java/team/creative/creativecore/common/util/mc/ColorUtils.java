package team.creative.creativecore.common.util.mc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3d;

import net.minecraft.core.Vec3i;

public class ColorUtils {
    
    public static class Color {
        
        public int red;
        public int green;
        public int blue;
        public int alpha;
        
        public Color(int color) {
            this.red = red(color);
            this.green = green(color);
            this.blue = blue(color);
            this.alpha = alpha(color);
        }
        
        public Color(Vec3i vec) {
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
            RenderSystem.setShaderColor(red / 255F, green / 255F, blue / 255F, alpha / 255F);
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
    public static final int LIGHT_BLUE = -16740609;
    public static final int ORANGE = -23296;
    public static final int YELLOW = -256;
    public static final int CYAN = 16711681;
    public static final int MAGENTA = -65281;
    public static final int BLACK = -16777216;
    
    public static int alpha(int color) {
        return color >> 24 & 255;
    }
    
    public static int red(int color) {
        return color >> 16 & 255;
    }
    
    public static int green(int color) {
        return color >> 8 & 255;
    }
    
    public static int blue(int color) {
        return color & 255;
    }
    
    public static float alphaF(int color) {
        return (color >> 24 & 255) / 255F;
    }
    
    public static float redF(int color) {
        return (color >> 16 & 255) / 255F;
    }
    
    public static float greenF(int color) {
        return (color >> 8 & 255) / 255F;
    }
    
    public static float blueF(int color) {
        return (color & 255) / 255F;
    }
    
    public static int rgba(int red, int green, int blue, int alpha) {
        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }
    
    public static int rgba(float red, float green, float blue, float alpha) {
        return (((int) (alpha * 255)) & 255) << 24 | (((int) (red * 255)) & 255) << 16 | (((int) (green * 255)) & 255) << 8 | ((int) (blue * 255)) & 255;
    }
    
    public static int rgb(int red, int green, int blue) {
        return (255 & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    }
    
    public static int rgb(float red, float green, float blue) {
        return (255 & 255) << 24 | (((int) (red * 255)) & 255) << 16 | (((int) (green * 255)) & 255) << 8 | ((int) (blue * 255)) & 255;
    }
    
    public static int rgb(Vec3i color) {
        return (255 & 255) << 24 | (color.getX() & 255) << 16 | (color.getY() & 255) << 8 | color.getZ() & 255;
    }
    
    public static Vec3i toIntVec(int color) {
        float r = color >> 16 & 255;
        float g = color >> 8 & 255;
        float b = color & 255;
        return new Vec3i(r, g, b);
    }
    
    public static Vector3d toVec(int color) {
        float r = color >> 16 & 255;
        float g = color >> 8 & 255;
        float b = color & 255;
        return new Vector3d(r / 255F, g / 255F, b / 255F);
    }
    
    public static boolean isDefault(int color) {
        return color == WHITE;
    }
    
    public static boolean isTransparent(int color) {
        return (color >> 24 & 255) < 255;
    }
    
    public static boolean isInvisible(int color) {
        return (color >> 24 & 255) == 0;
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
