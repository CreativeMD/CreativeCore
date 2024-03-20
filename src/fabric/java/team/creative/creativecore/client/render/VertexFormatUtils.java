package team.creative.creativecore.client.render;

import java.util.List;

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
        VERTEX_FORMAT_INT_SIZE = format.getIntegerSize();
        UV_OFFSET = -1;
        List<VertexFormatElement> elements = format.getElements();
        int offset = 0;
        for (int i = 0; i < elements.size(); i++) {
            switch (elements.get(i).getUsage()) {
                case POSITION -> POSITION_OFFSET = offset;
                case UV -> {
                    if (UV_OFFSET == -1)
                        UV_OFFSET = offset;
                }
                default -> {
                }
            }
            offset += elements.get(i).getByteSize();
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
