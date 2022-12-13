package team.creative.creativecore.common.util.mc;

import org.joml.Vector3d;

import net.minecraft.core.Vec3i;
import team.creative.creativecore.common.util.type.Color;

public class ColorUtils {
    
    public static enum ColorPart {
        RED(0xFF0000) {
            @Override
            public int get(Color color) {
                return color.getRed();
            }
            
            @Override
            public void set(Color color, int value) {
                color.setRed(value);
            }
        },
        GREEN(0x00FF00) {
            @Override
            public int get(Color color) {
                return color.getGreen();
            }
            
            @Override
            public void set(Color color, int value) {
                color.setGreen(value);
            }
        },
        BLUE(0x0000FF) {
            @Override
            public int get(Color color) {
                return color.getBlue();
            }
            
            @Override
            public void set(Color color, int value) {
                color.setBlue(value);
            }
        },
        ALPHA(0xFF000000) {
            @Override
            public int get(Color color) {
                return color.getAlpha();
            }
            
            @Override
            public void set(Color color, int value) {
                color.setAlpha(value);
            }
        };
        
        public final int code;
        
        private ColorPart(int code) {
            this.code = code;
        }
        
        public abstract int get(Color color);
        
        public abstract void set(Color color, int value);
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
    
    public static int setAlpha(int color, int alpha) {
        return (alpha & 255) << 24 | (color & ~ColorPart.ALPHA.code);
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
