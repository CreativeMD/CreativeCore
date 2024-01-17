package team.creative.creativecore.client.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.IQuadTransformer;
import team.creative.creativecore.client.render.box.RenderBox;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class CreativeBakedQuad extends BakedQuad {
    
    public final RenderBox cube;
    
    public CreativeBakedQuad(int[] vertices, BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor) {
        this(vertices, quad, cube, tintedColor, shouldOverrideColor, quad.getDirection());
    }
    
    public CreativeBakedQuad(int[] vertices, BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, Direction facing) {
        super(vertices, shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.isShade());
        this.cube = cube;
    }
    
    public void updateAlpha() {
        int alpha = ColorUtils.alpha(cube.color);
        if (alpha == 255)
            return;
        for (int k = 0; k < 4; k++) {
            int index = k * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
            getVertices()[index] = ColorUtils.setAlpha(getVertices()[index], alpha);
        }
    }
    
}
