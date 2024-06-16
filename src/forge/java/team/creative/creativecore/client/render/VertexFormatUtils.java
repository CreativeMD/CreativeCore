package team.creative.creativecore.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class VertexFormatUtils {
    
    private static int VERTEX_FORMAT_SIZE;
    private static int VERTEX_FORMAT_INT_SIZE;
    private static int POSITION_OFFSET;
    private static int UV_OFFSET;
    
    static {
        update();
    }
    
    public static void update() {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        VERTEX_FORMAT_SIZE = format.getVertexSize();
        VERTEX_FORMAT_INT_SIZE = VERTEX_FORMAT_SIZE * 4;
        UV_OFFSET = -1;
        for (VertexFormatElement element : format.getElements())
            switch (element.usage()) {
                case POSITION -> POSITION_OFFSET = format.getOffset(element);
                case UV -> {
                    if (UV_OFFSET == -1)
                        UV_OFFSET = format.getOffset(element);
                }
            }
    }
    
    public static int blockFormatSize() {
        return VERTEX_FORMAT_SIZE;
    }
    
    public static int blockFormatIntSize() {
        return VERTEX_FORMAT_INT_SIZE;
    }
    
    public static int blockPositionOffset() {
        return POSITION_OFFSET;
    }
    
    public static int blockUvOffset() {
        return UV_OFFSET;
    }
    
}
