package com.creativemd.creativecore.client.rendering.model;

import com.creativemd.creativecore.client.rendering.RenderBox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class CreativeBakedQuad extends BakedQuad {
    
    public static TextureAtlasSprite missingSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getTextureMap().getMissingSprite();
    public static final ThreadLocal<CreativeBakedQuad> lastRenderedQuad = new ThreadLocal<>();
    public final RenderBox cube;
    public boolean shouldOverrideColor;
    
    public CreativeBakedQuad(EnumFacing facing) {
        super(new int[28], -1, facing, missingSprite, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
        cube = null;
    }
    
    public CreativeBakedQuad(BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, EnumFacing facing) {
        this(quad, cube, tintedColor, shouldOverrideColor, facing, false);
    }
    
    private CreativeBakedQuad(BakedQuad quad, RenderBox cube, int tintedColor, boolean shouldOverrideColor, EnumFacing facing, boolean something) {
        super(copyArray(quad.getVertexData()), shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
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
