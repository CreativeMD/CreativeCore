package team.creative.creativecore.client.render.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import team.creative.creativecore.client.render.box.RenderBox;

public class CreativeBakedQuad extends BakedQuad {
    
    //public static TextureAtlasSprite missingSprite = MissingTextureSprite.getTexture();
    public static final ThreadLocal<CreativeBakedQuad> lastRenderedQuad = new ThreadLocal<>();
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
    
    @Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer) {
        lastRenderedQuad.set(this);
        net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
        lastRenderedQuad.set(null);
    }
    
}
