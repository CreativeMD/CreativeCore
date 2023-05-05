package team.creative.creativecore.client.render.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import team.creative.creativecore.client.VertexFormatExtender;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class CreativeBakedQuad extends BakedQuad {
    
    public static final int STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();
    public static final int POSITION = findOffset(DefaultVertexFormat.ELEMENT_POSITION);
    public static final int COLOR = findOffset(DefaultVertexFormat.ELEMENT_COLOR);
    public static final int UV0 = findOffset(DefaultVertexFormat.ELEMENT_UV0);
    public static final int UV1 = findOffset(DefaultVertexFormat.ELEMENT_UV1);
    public static final int UV2 = findOffset(DefaultVertexFormat.ELEMENT_UV2);
    public static final int NORMAL = findOffset(DefaultVertexFormat.ELEMENT_NORMAL);
    
    private static int findOffset(VertexFormatElement element) {
        // Divide by 4 because we want the int offset
        var index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : ((VertexFormatExtender) DefaultVertexFormat.BLOCK).getOffset(index) / 4;
    }
    
    public final RenderBox cube;
    public boolean shouldOverrideColor;
    
    public CreativeBakedQuad(BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, Direction facing) {
        this(quad, cube, tintedColor, shouldOverrideColor, facing, false);
    }
    
    private CreativeBakedQuad(BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, Direction facing, boolean something) {
        super(copyArray(quad.getVertices()), shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.isShade());
        this.cube = cube;
        this.shouldOverrideColor = shouldOverrideColor;
    }
    
    private static int[] copyArray(int[] array) {
        int[] newarray = new int[array.length];
        for (int i = 0; i < array.length; i++)
            newarray[i] = array[i];
        return newarray;
    }
    
    public void updateAlpha() {
        int alpha = ColorUtils.alpha(cube.color);
        if (alpha == 255)
            return;
        for (int k = 0; k < 4; k++) {
            int index = k * STRIDE + COLOR;
            getVertices()[index] = ColorUtils.setAlpha(getVertices()[index], alpha);
        }
    }
    
}
