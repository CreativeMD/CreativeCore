package team.creative.creativecore.client.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import team.creative.creativecore.client.render.box.RenderBox;

public class CreativeBakedQuad extends BakedQuad {
    
    public final RenderBox cube;
    
    public CreativeBakedQuad(int[] vertices, BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor) {
        this(vertices, quad, cube, tintedColor, shouldOverrideColor, quad.getDirection());
    }
    
    public CreativeBakedQuad(int[] vertices, BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, Direction facing) {
        super(vertices, shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.isShade());
        this.cube = cube;
    }
    
    public void updateAlpha() {}
    
}
