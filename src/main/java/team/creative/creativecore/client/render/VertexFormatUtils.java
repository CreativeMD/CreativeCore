package team.creative.creativecore.client.render;

import java.util.List;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class VertexFormatUtils {
    
    private static int vertexFormatSize;
    private static int POSITION_OFFSET;
    private static int UV_OFFSET;
    
    static {
        update();
    }
    
    public static void update() {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        vertexFormatSize = format.getVertexSize();
        UV_OFFSET = -1;
        List<VertexFormatElement> elements = format.getElements();
        for (int i = 0; i < elements.size(); i++)
            switch (elements.get(i).getUsage()) {
                case POSITION -> POSITION_OFFSET = format.getOffset(i);
                case UV -> {
                    if (UV_OFFSET == -1)
                        UV_OFFSET = format.getOffset(i);
                }
            }
    }
    
    public static int blockFormatSize() {
        return vertexFormatSize;
    }
    
    public static int blockPositionOffset() {
        return POSITION_OFFSET;
    }
    
    public static int blockUvOffset() {
        return UV_OFFSET;
    }
    
}
